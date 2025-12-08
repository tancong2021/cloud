/**
 * Copyright: 2019-2020，小树苗(www.xiaosm.cn)
 * FileName: OperatorMapper
 * Author:   Young
 * Date:     2020/6/16 10:34
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * Young         修改时间           版本号             描述
 */
package com.tancong.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.tancong.core.entity.DbLog;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface LogMapper extends BaseMapper<DbLog> {

}