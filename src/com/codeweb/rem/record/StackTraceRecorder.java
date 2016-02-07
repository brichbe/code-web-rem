package com.codeweb.rem.record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeweb.rem.model.MultiThreadStackTraceEntries;
import com.codeweb.rem.model.StackTraceEntry;
import com.codeweb.rem.model.StackTraceExecution;
import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StackTraceRecorder
{
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

  private final ProjectStructure projStructure;
  private final StackTraceExecution recordedExecution;

  private final Map<String, Collection<String>> validPackagesAndTheirSource = new HashMap<>();

  public StackTraceRecorder(ProjectStructure projStructure)
  {
    super();
    this.projStructure = projStructure;
    this.recordedExecution = new StackTraceExecution(projStructure.getProjName(), System.currentTimeMillis());

    this.buildPackageFilter(projStructure.getTopPackages());
  }

  private void buildPackageFilter(Collection<ProjectPackage> packages)
  {
    Collection<String> srcFileNames = new ArrayList<>();
    for (ProjectPackage pkg : packages)
    {
      Collection<ProjectSrcFile> srcFiles = pkg.getSrcFiles();
      if (!srcFiles.isEmpty())
      {
        srcFileNames.clear();
        for (ProjectSrcFile srcFile : srcFiles)
        {
          String filename = srcFile.getName();
          String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
          System.out.println("Adding source file name: " + nameWithoutExt);
          srcFileNames.add(nameWithoutExt);
        }
        this.validPackagesAndTheirSource.put(pkg.getName(), srcFileNames);
      }
      this.buildPackageFilter(pkg.getSubPackages());
    }
  }

  public void record(Map<Thread, StackTraceElement[]> allStackTraces)
  {
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
            Collection<String> srcFileNames = validPackagesAndTheirSource.get(pkg);
            if (srcFileNames != null)
            {
              System.out.println("Found pkg: " + pkg);
              String cls = pkgAndClass.substring(classSeparatorIndex + 1);
              if (srcFileNames.contains(cls))
              {
                System.out.println("Also found class: " + cls);
                pkgs.add(pkg);
                classes.add(cls);
              }
              else
              {
                System.err.println("Didn't find class: " + cls);
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
      if (!allThreadEntries.getEntries().isEmpty())
      {
        this.recordedExecution.addEntry(allThreadEntries);
      }
    }
  }

  public void finalize() throws IOException
  {
    // TODO: share code with ssa..

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(recordedExecution);

    String projName = projStructure.getProjName().replaceAll("[^a-zA-Z0-9.-]", "_");
    String dtgStr = dateFormat.format(new Date(recordedExecution.getStartDtg()));
    File f = new File(projName + "_" + dtgStr + ".rem");

    FileWriter writer = new FileWriter(f);
    writer.write(json);
    writer.close();
  }
}
