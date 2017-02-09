package com.codeweb.rem.model;

public class StackTraceExecutingClassRecording
{
  private final String pkgName;
  private final String className;

  public StackTraceExecutingClassRecording(String pkg, String cls)
  {
    this.pkgName = pkg;
    this.className = cls;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StackTraceExecutingClassRecording other = (StackTraceExecutingClassRecording) obj;
    if (className == null)
    {
      if (other.className != null)
        return false;
    }
    else if (!className.equals(other.className))
      return false;
    if (pkgName == null)
    {
      if (other.pkgName != null)
        return false;
    }
    else if (!pkgName.equals(other.pkgName))
      return false;
    return true;
  }

}
