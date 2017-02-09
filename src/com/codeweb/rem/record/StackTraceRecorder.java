package com.codeweb.rem.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeweb.rem.model.MultiThreadStackTraceEntries;
import com.codeweb.rem.model.StackTraceEntry;
import com.codeweb.rem.model.StackTraceExecution;
import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.ssa.util.FileIO;

public class StackTraceRecorder
{
  private final StackTraceExecution recordedExecution;
  private final Map<String, Collection<String>> validPackagesAndTheirClasses = new HashMap<>();

  public StackTraceRecorder(ProjectStructure projStructure)
  {
    super();
    this.recordedExecution = new StackTraceExecution(projStructure.getProjName(), System.currentTimeMillis());

    this.buildPackageFilter(projStructure.getTopPackages());
  }

  private void buildPackageFilter(Collection<ProjectPackage> packages)
  {
    for (ProjectPackage pkg : packages)
    {
      Collection<ProjectSrcFile> srcFiles = pkg.getSrcFiles();
      if (!srcFiles.isEmpty())
      {
        Collection<String> srcClassNames = new ArrayList<>();
        for (ProjectSrcFile srcFile : srcFiles)
        {
          srcClassNames.add(srcFile.getClassName());
        }
        this.validPackagesAndTheirClasses.put(pkg.getName(), srcClassNames);
      }
      this.buildPackageFilter(pkg.getSubPackages());
    }
  }

  public void recordCurrentState()
  {
    Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
    if (allStackTraces != null && !allStackTraces.isEmpty())
    {
      MultiThreadStackTraceEntries allThreadEntries = new MultiThreadStackTraceEntries(System.currentTimeMillis());
      for (Thread thread : allStackTraces.keySet())
      {
        StackTraceElement[] stElements = allStackTraces.get(thread);
        if (stElements.length > 0)
        {
          List<String> pkgs = new ArrayList<>();
          List<String> classes = new ArrayList<>();
          for (StackTraceElement ste : stElements)
          {
            String pkgAndClass = ste.getClassName();
            int classSeparatorIndex = pkgAndClass.lastIndexOf('.');
            String pkg = pkgAndClass.substring(0, classSeparatorIndex);
            Collection<String> validClassNames = validPackagesAndTheirClasses.get(pkg);
            if (validClassNames != null)
            {
              String cls = pkgAndClass.substring(classSeparatorIndex + 1);
              int innerClassIndex = cls.lastIndexOf('$');
              if (innerClassIndex != -1)
              {
                cls = cls.substring(0, innerClassIndex);
              }
              if (validClassNames.contains(cls))
              {
                pkgs.add(pkg);
                classes.add(cls);
              }
              else
              {
                System.err.println("Didn't find class: " + pkg + "." + cls);
              }
            }
          }

          if (!pkgs.isEmpty() && !classes.isEmpty())
          {
            StackTraceEntry entry = new StackTraceEntry(thread.getName(), pkgs, classes);
            allThreadEntries.addEntry(entry);
          }
        }
      }
      if (allThreadEntries.getNumEntries() > 0)
      {
        this.recordedExecution.addEntry(allThreadEntries);
      }
    }
  }

  public void finalize() throws IOException
  {
    FileIO.writeJson(recordedExecution, recordedExecution.getProjName(), recordedExecution.getStartDtg(), ".rem");
  }
}
