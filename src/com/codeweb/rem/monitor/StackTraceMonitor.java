package com.codeweb.rem.monitor;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.codeweb.rem.record.StackTraceRecorder;
import com.codeweb.ssa.model.ProjectStructure;

public class StackTraceMonitor
{
  private final Timer timer = new Timer(true);
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
    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
//        System.out.println("--- Start ---");
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        recorder.record(allStackTraces);
//        System.out.println("--- End ---");
      }
    }, monitorFreq, monitorFreq);
  }

  public void stop()
  {
    timer.cancel();
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
