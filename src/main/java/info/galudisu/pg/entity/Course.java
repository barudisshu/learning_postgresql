package info.galudisu.pg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Galudisu
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course")
public class Course implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId("c_no")
  private String cNo;

  @TableField("title")
  private String title;

  @TableField("hours")
  private Integer hours;
}
