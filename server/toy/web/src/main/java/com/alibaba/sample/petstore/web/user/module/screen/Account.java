/*
 * Copyright (c) 2002-2012 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.sample.petstore.web.user.module.screen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.sample.petstore.biz.UserManager;
import com.alibaba.sample.petstore.dal.dataobject.User;
import com.alibaba.sample.petstore.web.common.PetstoreUser;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;

public class Account {
    @Autowired
    private UserManager userManager;

    public void execute(Context context) throws Exception {
        User user = userManager.getUser(PetstoreUser.getCurrentUser().getId());
        context.put("user", user);
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
      response.setContentType("text/html");
      //STEP2-1:获取输出流;
      PrintWriter out = response.getWriter();
      //STEP2-2:准备数据;
      ArrayList myList = new ArrayList();
      myList.add("ABC");
      myList.add("DEF");
      myList.add("GHI");
      //STEP2-3:初始化VelocityContext对象;
      VelocityContext vtlContext = new VelocityContext();
      //STEP2-4:把准备好的数据放入到VelocityContext对象中;
      vtlContext.put("myList", myList);
      //STEP2-5:初始化Velocity引擎;
      String path = null;
      VelocityEngine velocityEngine = new VelocityEngine();
      Properties prop = new Properties();
      path = request.getRealPath("/");
      prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
      velocityEngine.init(prop);
      //STEP2-6:获取模板文件对象;
      Template template = null;
      template = velocityEngine.getTemplate("test.vm");
      //STEP2-7:把模版对象合并到输出流中;
      if(template != null)
      {
        template.merge(vtlContext, out);
      }
      //STEP2-8:输出技术结束之后,关闭输出流;
      out.flush();
      out.close();
    }
}
