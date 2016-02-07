package com.codeweb.rem.model;

import java.util.List;

public class StackTraceEntry
{
  private final String threadName;
  private final List<String> pkgNames;
  private final List<String> classNames;

  // TODO: restructure a bit, so for each time the stack is monitored you get a multithread entry,
  // which has one or more entries per thread, where for each thread is a list of package.class
  // entries, where that combo should only occur once within that sequence for that thread
  public StackTraceEntry(String thread, List<String> pkgs, List<String> classes)
  {
    super();
    this.threadName = thread;
    this.pkgNames = pkgs;
    this.classNames = classes;
  }

  public String getThreadName()
  {
    return threadName;
  }

  public List<String> getPkgNames()
  {
    return pkgNames;
  }

  public List<String> getClassNames()
  {
    return classNames;
  }
}
