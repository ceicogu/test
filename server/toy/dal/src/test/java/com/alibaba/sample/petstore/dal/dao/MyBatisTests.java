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

package com.alibaba.sample.petstore.dal.dao;

import com.alibaba.citrus.test.context.SpringextContextLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.persistent.UserMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/resources.xml", 
                                    "classpath:com/alibaba/sample/petstore/dal/spring/persistent.spring.xml" }, loader = SpringextContextLoader.class)
public class MyBatisTests extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void getUserById() {
        UserDO user = userMapper.getById(1L);
        int bb=2;

    }
}
