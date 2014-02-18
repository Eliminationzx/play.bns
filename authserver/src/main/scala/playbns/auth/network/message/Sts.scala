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

import akka.actor.IO
import akka.util.ByteString
import hexlab.morf.util.Base64
import scala.xml.XML

/**
 * This class ...
 *
 * @author hex1r0
 */
object Sts {
  implicit val byteOrder = java.nio.ByteOrder.LITTLE_ENDIAN

  private def trim(in: String) = in.trim
  private def ascii(bytes: ByteString): String = bytes.decodeString("US-ASCII")
  private def token(bytes: ByteString): String = bytes.decodeString("US-ASCII")
  private def arg(sb: StringBuilder, k: String, v: String) {
    sb.append(k).append(":").append(v).append(token(Token.CRLF))
  }

  def request2(id: String, body: String, session: Int) = {
    val args = Map(("l", body.size.toString), ("s", session.toString))

    Request(RequestHeader(id), args, body)
  }

  def request2(id: String, body: String) = {
    val args = Map(("l", body.size.toString))

    Request(RequestHeader(id), args, body)
  }

  def response(body: String, session: Int): Response = response("200", "OK", body, session)

  def response(stateCode: String, stateName: String, body: String, session: Int): Response = {
    val args = Map(("l", body.size.toString), ("s", session.toString + "R"))
    Response(ResponseHeader(stateCode, stateName), args, body)
  }

  object PacketId {
    val STS_CONNECT = "/Sts/Connect"
    val AUTH_LOGINSTART = "/Auth/LoginStart"
    val AUTH_KEYDATA = "/Auth/KeyData"
    val AUTH_LOGINFINISH = "/Auth/LoginFinish"
    val AUTH_LOGOUT = "/Auth/LogoutMyClient"
    val AUTH_PING = "/Sts/Ping"
    val AUTH_REQUESTTOKEN = "/Auth/RequestToken"
    val AUTH_GETMYUSERINFO = "/Auth/GetMyUserInfo"
    val AUTH_REQUESTGAMETOKEN = "/Auth/RequestGameToken"
    val GAMEACCOUNT_LISTMYACCOUNTS = "/GameAccount/ListMyAccounts"
    val PRESENCE_USERINFO = "/Presence/UserInfo"
    val SECONDPASSWORD_GETSTATUS = "/SecondPassword/GetStatus"
    val SLOT_LISTSLOTS = "/Slot/ListSlots"
    val STS = "STS/1.0"
  }

  object Token {
    val SPACE = ByteString(" ")
    val CRLF = ByteString("\r\n")
    val COLON = ByteString(":")
    val PATH = ByteString("/")
  }

  case class RequestHeader(packetId: String)
  case class ResponseHeader(stateCode: String, stateName: String)

  case class Request(header: RequestHeader, args: Map[String, String], body: String) {
    def session: Int = {
      args.get("s") match {
        case s: Some[String] => s.get.split("R")(0).toInt
        case None => -1
      }
    }

    def toByteString = {
      val sb = new StringBuilder

      // header
      sb.append("POST")
        .append(" ")
        .append(header.packetId)
        .append(" ")
        .append(PacketId.STS)
        .append(token(Token.CRLF))

      // args
      //arg(sb, "l", body.size.toString)
      args foreach (e => arg(sb, e._1, e._2))
      sb.append(token(Token.CRLF))

      // body
      sb.append(body)

      ByteString(sb.toString())
    }
  }

  case class Response(header: ResponseHeader, args: Map[String, String], body: String) {
    def session = {
      args.get("s") match {
        case s: Some[String] => s.get.split("R")(0).toInt
        case _ => throw new IllegalStateException()
      }
    }

    def toByteString = {
      val sb = new StringBuilder

      // header
      sb.append("STS/1.0")
        .append(" ")
        .append(header.stateCode)
        .append(" ")
        .append(header.stateName)
        .append(token(Token.CRLF))

      // args
      //arg(sb, "l", body.size.toString)
      args foreach (e => arg(sb, e._1, e._2))
      sb.append(token(Token.CRLF))

      // body
      sb.append(body)

      ByteString(sb.toString())
    }
  }

  abstract class StsParser {
    def parse: IO.Iteratee[_]

    def parseHeader: IO.Iteratee[_]

    def parseArgs = {
      def parse =
        for {
          name <- IO takeUntil Token.COLON // arg name
          value <- IO takeUntil Token.CRLF // arg value
        } yield {
          (trim(ascii(name)), trim(ascii(value)))
        }

      def step(found: Map[String, String]): IO.Iteratee[Map[String, String]] =
        IO peek 2 flatMap {
          case Token.CRLF => IO takeUntil Token.CRLF flatMap (_ => IO Done found)
          case _ => parse flatMap (x => step(found updated(x._1, x._2)))
        }

      step(Map.empty)
    }

    def parseBody(args: Map[String, String]): IO.Iteratee[String] = {
      for {
        body <- args.get("l") match {
          case l: Some[String] =>
            IO.take(Integer.valueOf(l.get))
          case _ => // TODO check if this works
            IO.takeAll
        }
      } yield {
        ascii(body)
      }
    }
  }

  object RequestParser extends StsParser {
    override def parse: IO.Iteratee[Request] = {
      for {
        header <- parseHeader
        args <- parseArgs
        body <- parseBody(args)
      } yield {
        Request(header, args, body)
      }
    }

    override def parseHeader: IO.Iteratee[RequestHeader] = {
      for {
        _ <- IO.takeUntil(Token.SPACE) // POST
        packetId <- IO.takeUntil(Token.SPACE) // /Sts/Connect, /Auth/LoginStart, ...
        _ <- IO.takeUntil(Token.CRLF) // STS/1.0
      } yield {
        RequestHeader(trim(ascii(packetId)))
      }
    }
  }

  object ReplyParser extends StsParser {
    override def parse: IO.Iteratee[Response] = {
      for {
        header <- parseHeader
        args <- parseArgs
        body <- parseBody(args)
      } yield {
        Response(header, args, body)
      }
    }

    override def parseHeader: IO.Iteratee[ResponseHeader] = {
      for {
        _ <- IO.takeUntil(Token.SPACE) // STS/1.0
        stateCode <- IO.takeUntil(Token.SPACE) // 200, ...
        stateName <- IO.takeUntil(Token.CRLF) // OK, Err...
      } yield {
        ResponseHeader(trim(ascii(stateCode)), trim(ascii(stateName)))
      }
    }
  }

  // --------------------------------------------------------------------------

  object AuthLoginStart {
    def create(loginName: String, session: Int) = {
      val body = s"<Request>\n<LoginName>$loginName</LoginName>\n</Request>\n"
      request2(PacketId.AUTH_LOGINSTART, body, session)
    }

    def parse(request: Request) = {
      (XML.loadString(request.body) \\ "LoginName").text
    }
  }

  object AuthLoginStartResponse {
    def create(salt: Array[Byte], b: Array[Byte], session: Int) = {
      val data = ByteString.newBuilder
        .putInt(salt.length).putBytes(salt)
        .putInt(b.length).putBytes(b).result().toArray

      val body =
        "<Reply>\n" +
          "<KeyData>" + Base64.encodeToString(data, false) + "</KeyData>\n" +
          "</Reply>\n"

      response(body, session)
    }

    def parse(response: Response) = {
      val keyDataStr = (XML.loadString(response.body) \\ "KeyData").text
      val keyData = ByteString(Base64.decode(keyDataStr)).iterator

      val salt = new Array[Byte](keyData.getInt)
      keyData.getBytes(salt)

      val b = new Array[Byte](keyData.getInt)
      keyData.getBytes(b)

      (salt, b)
    }
  }

  object AuthKeyData {
    def create(a: Array[Byte], m: Array[Byte], session: Int) = {
      val data = ByteString.newBuilder
        .putInt(a.length).putBytes(a)
        .putInt(m.length).putBytes(m).result().toArray

      val body =
        "<Request>\n" +
          "<KeyData>" + Base64.encodeToString(data, false) + "</KeyData>\n" +
          "</Request>\n"

      request2(PacketId.AUTH_KEYDATA, body, session)
    }

    def parse(request: Request) = {
      val keyDataStr = (XML.loadString(request.body) \\ "KeyData").text
      val keyData = ByteString(Base64.decode(keyDataStr)).iterator

      val a = new Array[Byte](keyData.getInt)
      keyData.getBytes(a)

      val m = new Array[Byte](keyData.getInt)
      keyData.getBytes(m)

      (a, m)
    }
  }

  object AuthKeyDataResponse {
    def create(m2: Array[Byte], session: Int) = {
      val data = ByteString.newBuilder
        .putInt(m2.length).putBytes(m2).result().toArray

      val body =
        "<Reply>\n" +
          "<KeyData>" + Base64.encodeToString(data, false) + "</KeyData>\n" +
          "</Reply>\n"

      response(body, session)
    }

    def parse(response: Response) = {
      val keyDataStr = (XML.loadString(response.body) \\ "KeyData").text
      val keyData = ByteString(Base64.decode(keyDataStr)).iterator

      val m2 = new Array[Byte](keyData.getInt)
      keyData.getBytes(m2)

      m2
    }
  }

  object PresenceUserInfoRequest {
    def create(userId: String,
               userName: String) = {
      val body =
        "<Message>\n" +
          "<UserId>" + userId + "</UserId>\n" +
          "<UserCenter>1</UserCenter>\n" +
          "<UserName>" + userName + "</UserName>\n" +
          "<Status>online</Status>\n" +
          "<Aliases type=\"array\">\n" +
          "<Alias>bns:" + userName + "</Alias>\n" +
          "<Alias>" + userName + "</Alias>\n" +
          "</Aliases>\n" +
          "</Message>\n"

      request2(Sts.PacketId.PRESENCE_USERINFO, body)
    }
  }

  object AuthLoginFinishResponse {
    def create(userId: String,
               userName: String,
               accessMask: Long = 17179869183L,
               locationId: String = "F426B612-E4B6-403E-8406-7EC09FFD9C19",
               session: Int) = {
      val body =
        "<Reply>\n" +
          "<UserId>" + userId + "</UserId>\n" +
          "<UserCenter>1</UserCenter>\n" +
          "<Roles type=\"array\"/>\n" +
          "<LocationId>" + locationId + "</LocationId>\n" +
          "<AccessMask>" + accessMask + "</AccessMask>\n" +
          "<UserName>" + userName + "</UserName>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }

  object AuthRequestTokenResponse {
    def create(authToken: String = "RkY1QzlGOTMtN0VCRS1FMDExLTg5QjEtRTYxRjEzNUU5OTJGOjdCM0E5QTk2LTU4RjQtNEU5NS1BMUYzLTYzMUFEN0JBRUFCNQ==",
               session: Int) = {
      val body =
        "<Reply>\n" +
          "<AuthnToken>" + authToken + "</AuthnToken>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }

  object GameAccounListMyAccountsResponse {
    def create(userId: String,
               created: String = "2012-11-09T23:54:21.880+09:00",
               session: Int) = {
      val body =
        "<Reply type=\"array\">\n" +
          "<GameAccount>\n" +
          "<Alias>" + userId + "</Alias>\n" +
          "<Created>" + created + "</Created>\n" +
          "</GameAccount>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }

  object AuthGetMyUserInfoResponse {
    def create(userId: String,
               userName: String,
               fullUserName: String,
               status: Int = 1,
               created: String = "2011-08-04T18:46:14.797+09:00",
               session: Int) = {
      val body =
        "<Reply>\n" +
          "<UserId>" + userId + "</UserId>\n" +
          "<UserCenter>1</UserCenter>\n" +
          "<UserName>" + userName + "</UserName>\n" +
          "<LoginName>" + fullUserName + "</LoginName>\n" +
          "<UserStatus>" + status + "</UserStatus>\n" +
          "<Created>" + created + "</Created>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }

  object AuthRequestGameTokenResponse {
    def create(token: String = "B47F5C30-6795-4D9C-B74B-6C802D8616F2",
               session: Int) = {
      val body =
        "<Reply>\n" +
          "<Token>" + token + "</Token>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }

  object SecondPasswordGetStatusResponse {
    def create(session: Int) = {
      val body = "<Error code=\"3333\" server=\"107\" module=\"2\" line=\"811\"/>\n"

      response("400", "ErrSecondPasswordNotFound", body, session)
    }
  }

  object SlotListSlotsResponse {
    def create(session: Int) = {
      val body =
        "<Reply type=\"array\">\n" +
          "<Slot>\n" +
          "<SlotId>4061E0E8-942A-E211-A6FE-E41F136C9588</SlotId>\n" +
          "<AppGroupId>2</AppGroupId>\n" +
          "<SlotType>char</SlotType>\n" +
          "<SystemSlot>1</SystemSlot>\n" +
          "<Changed>2012-11-09T17:43:03Z</Changed>\n" +
          "<Registered>2012-11-09T17:43:03Z</Registered>\n" +
          "</Slot>\n" +
          "<Slot>\n" +
          "<SlotId>4161E0E8-942A-E211-A6FE-E41F136C9588</SlotId>\n" +
          "<AppGroupId>2</AppGroupId>\n" +
          "<SlotType>char</SlotType>\n" +
          "<SystemSlot>1</SystemSlot>\n" +
          "<Changed>2012-11-09T17:43:03Z</Changed>\n" +
          "<Registered>2012-11-09T17:43:03Z</Registered>\n" +
          "</Slot>\n" +
          "<Slot>\n" +
          "<SlotId>4261E0E8-942A-E211-A6FE-E41F136C9588</SlotId>\n" +
          "<AppGroupId>2</AppGroupId>\n" +
          "<SlotType>char</SlotType>\n" +
          "<SystemSlot>1</SystemSlot>\n" +
          "<Changed>2012-11-09T17:43:03Z</Changed>\n" +
          "<Registered>2012-11-09T17:43:03Z</Registered>\n" +
          "</Slot>\n" +
          "<Slot>\n" +
          "<SlotId>4361E0E8-942A-E211-A6FE-E41F136C9588</SlotId>\n" +
          "<AppGroupId>2</AppGroupId>\n" +
          "<SlotType>char</SlotType>\n" +
          "<SystemSlot>1</SystemSlot>\n" +
          "<Changed>2012-11-09T17:43:03Z</Changed>\n" +
          "<Registered>2012-11-09T17:43:03Z</Registered>\n" +
          "</Slot>\n" +
          "<Slot>\n" +
          "<SlotId>4461E0E8-942A-E211-A6FE-E41F136C9588</SlotId>\n" +
          "<AppGroupId>2</AppGroupId>\n" +
          "<SlotType>char</SlotType>\n" +
          "<SystemSlot>1</SystemSlot>\n" +
          "<Changed>2012-11-09T17:43:03Z</Changed>\n" +
          "<Registered>2012-11-09T17:43:03Z</Registered>\n" +
          "</Slot>\n" +
          "</Reply>\n"

      response(body, session)
    }
  }
}
