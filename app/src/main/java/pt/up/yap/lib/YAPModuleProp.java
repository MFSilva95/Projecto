/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package pt.up.yap.lib;

public class YAPModuleProp extends YAPProp {
  private transient long swigCPtr;

  protected YAPModuleProp(long cPtr, boolean cMemoryOwn) {
    super(yapJNI.YAPModuleProp_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(YAPModuleProp obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        yapJNI.delete_YAPModuleProp(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public YAPModule module() {
    return new YAPModule(yapJNI.YAPModuleProp_module(swigCPtr, this), true);
  }

}
