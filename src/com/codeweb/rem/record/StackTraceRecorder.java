package com.codeweb.rem.record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.codeweb.rem.model.StackTraceExecutingClassRecording;
import com.codeweb.rem.model.StackTraceRecordedExecution;
import com.codeweb.rem.model.StackTraceTimedRecording;
import com.codeweb.rem.model.StackTraceSingleThreadRecording;
import com.codeweb.ssa.model.ProjectPackage;
import com.codeweb.ssa.model.ProjectSrcFile;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.ssa.util.FileIO;

public class StackTraceRecorder
{
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private final long recordRate;
  private final StackTraceRecordedExecution recordedExecution;
  private final Map<String, Collection<String>> validPackagesAndTheirClasses = new HashMap<>();

  public StackTraceRecorder(ProjectStructure projStructure, long recordFreq)
  {
    super();
    this.recordedExecution = new StackTraceRecordedExecution(projStructure.getProjName(), System.currentTimeMillis());
    this.recordRate = recordFreq;

    this.buildPackageFilter(projStructure.getTopPackages());
  }

  public void start()
  {
    executor.scheduleAtFixedRate(new Runnable()
    {
      @Override
      public void run()
      {
        recordCurrentState();
      }
    }, recordRate, recordRate, TimeUnit.MILLISECONDS);
  }

  public void stop()
  {
    executor.shutdownNow();
    try
    {
      FileIO.writeJson(recordedExecution, recordedExecution.getProjName(), recordedExecution.getStartDtg(), ".rem");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
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

  private void recordCurrentState()
  {
    Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
    if (allStackTraces != null && !allStackTraces.isEmpty())
    {
      StackTraceTimedRecording stackTraceTimedRecording = new StackTraceTimedRecording(System.currentTimeMillis());
      for (Thread thread : allStackTraces.keySet())
      {
        StackTraceElement[] stElements = allStackTraces.get(thread);
        if (stElements.length > 0)
        {
          StackTraceSingleThreadRecording threadRecording = new StackTraceSingleThreadRecording(thread.getName());
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
                threadRecording.addRecording(new StackTraceExecutingClassRecording(pkg, cls));
              }
              else
              {
                System.err.println("Didn't find class: " + pkg + "." + cls);
              }
            }
          }
          if (threadRecording.getNumRecordings() > 0)
          {
            stackTraceTimedRecording.addRecording(threadRecording);
          }
        }
      }
      if (stackTraceTimedRecording.getNumRecordings() > 0)
      {
        this.recordedExecution.addRecording(stackTraceTimedRecording);
      }
    }
  }
}
