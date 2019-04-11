/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.gdal;

public class ProgressCallback {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ProgressCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ProgressCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        gdalJNI.delete_ProgressCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int run(double dfComplete, String pszMessage) {
    return gdalJNI.ProgressCallback_run(swigCPtr, this, dfComplete, pszMessage);
  }

  public ProgressCallback() {
    this(gdalJNI.new_ProgressCallback(), true);
  }

}
