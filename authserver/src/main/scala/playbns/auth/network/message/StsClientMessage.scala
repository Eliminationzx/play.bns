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

package playbns.auth.network.message

import akka.util.ByteString
import hexlab.morf.network.ClientMessage
import hexlab.morf.util.ByteArray._
import playbns.common.model.User
import playbns.common.util.BnSBinaryReader
import scala.xml.{XML, Elem}

/**
 * This class ...
 *
 * @author hex1r0
 */
trait StsClientMessage extends ClientMessage {
  var user: User = _
  var text: String = _

  def body: Elem = XML.loadString(text)

  def newBinaryReader(data: ByteArray) = new BnSBinaryReader {
    input = ByteString(data)
  }
}
