package com.codeweb.rem.model;

import java.util.ArrayList;
import java.util.List;

public class StackTraceSingleThreadRecording
{
  private final String threadName;
  private final List<StackTraceExecutingClassRecording> classRecordings = new ArrayList<>();

  public StackTraceSingleThreadRecording(String thread)
  {
    super();
    this.threadName = thread;
  }

  public String getThreadName()
  {
    return threadName;
  }

  public void addRecording(StackTraceExecutingClassRecording record)
  {
    if (!classRecordings.contains(record))
    {
      classRecordings.add(record);
    }
  }
  
  public int getNumRecordings()
  {
    return classRecordings.size();
  }
}
