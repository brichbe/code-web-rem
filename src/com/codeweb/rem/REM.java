package com.codeweb.rem;

import java.io.File;
import java.io.IOException;

import com.codeweb.rem.monitor.StackTraceMonitor;
import com.codeweb.ssa.model.ProjectStructure;
import com.codeweb.ssa.util.FileIO;

/**
 * Hook this class into the application you wish to monitor.
 */
public class REM
{
  private static StackTraceMonitor monitor = null;

  public static void start(String ssaFilePath, long monitorFreq) throws IOException
  {
    if (monitor != null)
    {
      stop();
    }

    File ssaFile = new File(ssaFilePath);
    if (ssaFile.exists() && ssaFile.canRead())
    {
      monitor = new StackTraceMonitor(FileIO.read(ssaFile, ProjectStructure.class), monitorFreq);
      monitor.start();
    }
  }

  public static void stop()
  {
    if (monitor != null)
    {
      monitor.stop();
    }
  }
}
