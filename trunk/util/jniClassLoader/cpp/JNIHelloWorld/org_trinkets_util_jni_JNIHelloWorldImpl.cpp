#include "stdafx.h"

#include "org_trinkets_util_jni_JNIHelloWorldImpl.h"
#include "malloc.h"

/**
 * Utility from http://www-128.ibm.com/developerworks/java/library/j-jninls/jninls.html
 */
char* jstringToWindows( JNIEnv * env, jstring jstr )
{
  int length = env->GetStringLength( jstr );
  const jchar* jcstr = env->GetStringChars( jstr, 0 );
  char* rtn = (char*)malloc( length*2+1 );
  int size = 0;
  size = WideCharToMultiByte( CP_ACP, 0, (LPCWSTR)jcstr, length, rtn,
                           (length*2+1), NULL, NULL );
  if( size <= 0 )
    return NULL;
  env->ReleaseStringChars( jstr, jcstr );
  rtn[size] = 0;
  return rtn;
}

JNIEXPORT void JNICALL Java_org_trinkets_util_jni_JNIHelloWorldImpl_sayHello0(JNIEnv *env, jobject obj, jstring msg) {
	::MessageBox(NULL, jstringToWindows(env, msg), "Hello JNI World", 0);
}
