#include <cstring>
#include <cstdlib>

#ifdef __cplusplus
extern "C" {
#endif
#include <libdynamite.h>
#ifdef __cplusplus
}
#endif


#include <jni.h>
#include "org_debian_paulliu_darnwdl_jni_DynamiteJNI.h"

jbyteArray charToJByteArray(JNIEnv *env, unsigned char *buf, int len) {
  jbyteArray array = env->NewByteArray(len);
  env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
  return array;
}

char *jByteArrayToChar(JNIEnv *env, jbyteArray buf) {
  char *chars = NULL;
  jbyte *bytes;

  bytes = env->GetByteArrayElements(buf, 0);
  int chars_len = env->GetArrayLength(buf);
  chars = new char[chars_len + 1];
  memset(chars, 0, chars_len+1);
  memcpy(chars, bytes, chars_len);
  env->ReleaseByteArrayElements(buf, bytes, 0);
  return chars;
}

typedef struct wdlpass1_explodeDataStruct {
  char *inputData;  /* input data */
  size_t current;
  size_t size;
  char *outputData;
  int outputDataSize;
} wdlpass1_explodeData;

/**
 * callback function for libdynamite to read from file to buffer.
 * This function controls the left size so libdynamite won't get next
 * header.
 * @param buffer the pointer of buffer to write
 * @param size the maximum size of the buffer
 * @param data wdlpass1_explodeData which to obtain the information of file
 * @return the size read from file
 */
size_t wdlpass1_dynamite_callback_read(void *buffer, size_t size, void *data) {
  int ret;
  wdlpass1_explodeData* ed;

  ed = ((wdlpass1_explodeData*)data);
  if (ed->current + size > ed->size) {
    size = ed->size - ed->current;
  }
  if (size <= 0) {
    return 0;
  }
  memcpy(buffer, &(ed->inputData[ed->current]), size);
  ed->current += size;
  ret = size;
  return ret;
}

/**
 * callback function for libdynamite to write to file from buffer.
 * This function just write toe buffer to the file
 * @param buffer the pointer of input buffer
 * @param size of the buffer
 * @param data wdlpass1_explodeData which to obtain the information of file
 * @return the size actually write to file
 */
size_t wdlpass1_dynamite_callback_write(void *buffer, size_t size, void *data) {
  size_t ret;

  wdlpass1_explodeData* ed;
  ed = ((wdlpass1_explodeData*)data);

  if (ed->outputData == NULL) {
    ed->outputData = (char *)malloc(size);
  } else {
    ed->outputData = (char *)realloc(ed->outputData, ed->outputDataSize + size);
  }
  memcpy(&(ed->outputData[ed->outputDataSize]), buffer, size);
  ed->outputDataSize += size;
  
  ret = size;
  return ret;
}

JNIEXPORT jbyteArray JNICALL Java_org_debian_paulliu_darnwdl_jni_DynamiteJNI_explode (JNIEnv *env, jobject obj, jbyteArray input) {
  wdlpass1_explodeData ed;
  char *buf;
  jbyteArray ret = NULL;
  if (input == NULL) {
    return NULL;
  }
  buf = jByteArrayToChar(env, input);

  ed.inputData = buf;
  ed.current = 0;
  ed.size = env->GetArrayLength(input);
  ed.outputData = NULL;
  ed.outputDataSize = 0;

  dynamite_explode(wdlpass1_dynamite_callback_read,wdlpass1_dynamite_callback_write,&ed);
  
  delete[] buf;

  ret = charToJByteArray(env, (unsigned char *)ed.outputData, ed.outputDataSize);
  if (ed.outputData != NULL) {
    free(ed.outputData);
  }
  
  return ret;
}
