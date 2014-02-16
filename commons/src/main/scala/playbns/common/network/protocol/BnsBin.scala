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

package playbns.common.network.protocol

import akka.actor.IO
import akka.util.ByteString
import java.nio.ByteOrder

/**
 * This class ...
 *
 * @author hex1r0
 */
object BnsBin {
  implicit val byteOrder = ByteOrder.LITTLE_ENDIAN

  case class Frame(header: Header, body: ByteString)
  case class Header(bodyLength: Int, cipherId: Int)
  case class Frame2(frameLength: Int, frameId: Int, body: ByteString)

  def parse: IO.Iteratee[Frame] = {
    for {
      header <- parseHeader
      body <- IO.take(header.bodyLength)
    } yield {
      Frame(header, body)
    }
  }

  def parseDecoded(bs: ByteString) = {
    val headerLength = 4
    val it = bs.iterator

    val frameLength = it.getShort & 0xffff
    val frameId = it.getShort & 0xffff

    val body = bs.slice(headerLength, frameLength)

    Frame2(frameLength, frameId, body)
  }

  def createFrame1(body: ByteString, aesId: Int): ByteString = {
    val header = (body.length / 4 & 0xfff) | (aesId + 4) << 0x0c
    val b = ByteString.newBuilder
    b.putShort(header)
    b.append(body)
    b.result()
  }

  private def parseHeader: IO.Iteratee[Header] = {
    for {
      raw <- IO.take(2)
    } yield {
      val _16bit = raw.iterator.getShort
      val cipherId = ((_16bit >> 0x0c) - 4) & 0xf
      val len = (_16bit & 0x0fff) * 4 & 0xfff
      val h = Header(len, cipherId)
      h
    }
  }

  def createFrame2(frameId: Int, body: ByteString) = {
    val frameLength = body.length + 4
    val builder = ByteString.newBuilder
    builder.putShort(frameLength)
    builder.putShort(frameId)
    builder.append(body)
    builder.result()
  }

}
