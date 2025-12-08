/**
 * Copyright: 2019-2020，小树苗(www.xiaosm.cn)
 * FileName: Operator
 * Author:   Young
 * Date:     2020/6/16 10:35
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * Young         修改时间           版本号             描述
 */
package com.tancong.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tancong.common.entity.Log;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ==============================
 * 〈数据库日志〉
 * ==============================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/04
 */
@Data
@Accessors(chain = true)
@TableName("log")
public class DbLog extends Log {

    @TableId(type = IdType.AUTO)
    private Integer id;

}