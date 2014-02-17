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

package playbns.auth

import hexlab.morf.core.ExecutionScope
import hexlab.morf.executor.MessageHandler
import hexlab.morf.util.ClassUtil._
import hexlab.morf.util.Reflection._

/**
 * This class ...
 *
 * @author hex1r0
 */
object HandlerUtil {
  def loadAllFrom(parent: Class[_], packageName: String): Stream[(Class[ExecutionScope], Class[MessageHandler])] = {
    val m = scala.reflect.runtime.universe.runtimeMirror(HandlerUtil.getClass.getClassLoader)
    val all = parsePackage(parent, packageName) map {
      case clazz if clazz.isChildOf[MessageHandler] =>
        findClassAnnotation[ExecutionScope](m, clazz) map { a =>
          (a.getClass.asInstanceOf[Class[ExecutionScope]], clazz.asInstanceOf[Class[MessageHandler]])
        }

      case _ => None
    }

    all.flatten
  }
}
