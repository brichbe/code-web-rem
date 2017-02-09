package com.codeweb.rem.monitor;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.codeweb.rem.record.StackTraceRecorder;
import com.codeweb.ssa.model.ProjectStructure;

public class StackTraceMonitor
{
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private final long monitorFreq;
  private final StackTraceRecorder recorder;

  public StackTraceMonitor(ProjectStructure projStructure, long monitorFreq)
  {
    super();
    this.recorder = new StackTraceRecorder(projStructure);
    this.monitorFreq = monitorFreq;
  }

  public void start()
  {
    executor.scheduleAtFixedRate(new Runnable()
    {
      @Override
      public void run()
      {
        recorder.recordCurrentState();

      }
    }, monitorFreq, monitorFreq, TimeUnit.MILLISECONDS);
  }

  public void stop()
  {
    executor.shutdownNow();
    try
    {
      recorder.finalize();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
