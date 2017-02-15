/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package pt.up.yap.lib;

public class YAPAtom {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected YAPAtom(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(YAPAtom obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        yapJNI.delete_YAPAtom(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public YAPAtom(String s) {
    this(yapJNI.new_YAPAtom__SWIG_0(s), true);
  }

  public YAPAtom(SWIGTYPE_p_wchar_t s) {
    this(yapJNI.new_YAPAtom__SWIG_1(SWIGTYPE_p_wchar_t.getCPtr(s)), true);
  }

  public YAPAtom(String s, long len) {
    this(yapJNI.new_YAPAtom__SWIG_2(s, len), true);
  }

  public YAPAtom(SWIGTYPE_p_wchar_t s, long len) {
    this(yapJNI.new_YAPAtom__SWIG_3(SWIGTYPE_p_wchar_t.getCPtr(s), len), true);
  }

  public String getName() {
    return yapJNI.YAPAtom_getName(swigCPtr, this);
  }

  public String text() {
    return yapJNI.YAPAtom_text(swigCPtr, this);
  }

  public SWIGTYPE_p_Prop getProp(PropTag tag) {
    return new SWIGTYPE_p_Prop(yapJNI.YAPAtom_getProp(swigCPtr, this, tag.swigValue()), true);
  }

}
