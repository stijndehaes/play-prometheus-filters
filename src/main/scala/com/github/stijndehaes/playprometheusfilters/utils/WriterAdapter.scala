package com.github.stijndehaes.playprometheusfilters.utils

import java.io.Writer

class WriterAdapter(buffer: StringBuilder) extends Writer {

  override def write(charArray: Array[Char], offset: Int, length: Int): Unit = {
    buffer ++= new String(new String(charArray, offset, length).getBytes("UTF-8"), "UTF-8")
  }

  override def flush(): Unit = {}

  override def close(): Unit = {}
}
