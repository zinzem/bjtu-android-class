#include <jni.h>
#include <android/log.h>


jstring Java_com_example_nativehelloworld_MainActivity_invokeNativeFunction(JNIEnv* env, jobject javaThis) {
  return (*env)->NewStringUTF(env, "Hello World!");
}
