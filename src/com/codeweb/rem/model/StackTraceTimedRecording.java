package com.codeweb.rem.model;

import java.util.ArrayList;
import java.util.List;

public class StackTraceTimedRecording
{
  private final long dtg;
  private final List<StackTraceSingleThreadRecording> threadRecordings = new ArrayList<>();

  public StackTraceTimedRecording(long dtg)
  {
    super();
    this.dtg = dtg;
  }

  public long getDtg()
  {
    return dtg;
  }

  public void addRecording(StackTraceSingleThreadRecording record)
  {
    this.threadRecordings.add(record);
  }

  public int getNumRecordings()
  {
    return threadRecordings.size();
  }
}
