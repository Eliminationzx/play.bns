/*
 * This file is part of PlayBnS
 *                      <https://github.com/HeXLaB/play.bns>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013-2014
 *               HeXLaB Team
 *                           All rights reserved
 */

package bns.common.util

import akka.util.{ByteIterator, ByteString}
import hexlab.morf.util.BinaryReader
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets._

/**
 * This class ...
 *
 * @author hex1r0
 */
trait BnSBinaryReader extends BinaryReader {
  private implicit val BYTE_ORDER = ByteOrder.LITTLE_ENDIAN

  private var _input: ByteString = _
  private var _it: ByteIterator = _

  def input = _input

  def input_=(input: ByteString) = {
    _input = input
    _it = input.iterator
  }

  protected def readByte: Byte = _it.getByte

  protected def readShort: Short = _it.getShort

  protected def readInt: Int = _it.getInt

  protected def readLong: Long = _it.getLong

  protected def readFloat: Float = _it.getFloat

  protected def readDouble: Double = _it.getDouble

  protected def readBytes(count: Int): Array[Byte] = {
    val out = new Array[Byte](count)
    readBytes(out)
    out
  }

  protected def readBytes(out: Array[Byte]): Unit = _it.getBytes(out)

  protected def skip(count: Int): Unit = _it.drop(count)

  protected def readUtf16: String = {
    val len = (readShort & 0xFFFF) * 2
    new String(readBytes(len), UTF_16LE)
  }

  protected def readAscii: String = {
    val len = readShort & 0xFFFF
    new String(readBytes(len), US_ASCII)
  }

  protected def readS = readUtf16

  protected def ascii = readAscii
  
  protected def utf16 = readUtf16
}
