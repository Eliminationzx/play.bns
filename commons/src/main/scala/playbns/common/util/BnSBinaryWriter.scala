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

package playbns.common.util

import akka.util.{ByteStringBuilder, ByteString}
import hexlab.morf.util.BinaryWriter
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets._

/**
 * This class ...
 *
 * @author hex1r0
 */
trait BnSBinaryWriter extends BinaryWriter[BnSBinaryWriter] {
  private implicit val BYTE_ORDER = ByteOrder.LITTLE_ENDIAN

  private val _bb: ByteStringBuilder = ByteString.newBuilder

  def writeByte(v: Int): BnSBinaryWriter = {
    _bb.putByte(v.toByte)
    this
  }

  def writeShort(v: Int): BnSBinaryWriter = {
    _bb.putShort(v.toShort)
    this
  }

  def writeInt(v: Int): BnSBinaryWriter = {
    _bb.putInt(v)
    this
  }

  def writeLong(v: Long): BnSBinaryWriter = {
    _bb.putLong(v)
    this
  }

  def writeFloat(v: Float): BnSBinaryWriter = {
    _bb.putFloat(v)
    this
  }

  def writeDouble(v: Double): BnSBinaryWriter = {
    _bb.putDouble(v)
    this
  }

  def writeBytes(v: Array[Byte]): BnSBinaryWriter = {
    _bb.putBytes(v)
    this
  }

  def fill(v: Int, count: Int): BnSBinaryWriter = {
    val b = v.toByte
    (0 until count).foreach(_ => _bb.putByte(b))
    this
  }

  def writeAscii(v: String): BnSBinaryWriter = {
    _bb.putShort(v.length)
    _bb.putBytes(v.getBytes(US_ASCII))
    this
  }

  def writeUtf16(v: String): BnSBinaryWriter = {
    _bb.putShort(v.length)
    _bb.putBytes(v.getBytes(UTF_16LE))
    this
  }

  def writeUtf16Nt(v: String): BnSBinaryWriter = {
    _bb.putBytes(v.getBytes(UTF_16LE))
    _bb.putShort(0)
    this
  }

  def write(v: BnSBinaryWriter): BnSBinaryWriter = {
    writeBytes(v._bb.result().toArray)
    this
  }

  def newBlock: BnSBinaryWriter = {
    new BnSBinaryWriter {}
  }

  def length = _bb.length

  def result = _bb.result()
}
