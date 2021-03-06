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
@TableName("student")
public class Student implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId("s_id")
  private Integer sId;

  @TableField("name")
  private String name;

  @TableField("start_year")
  private Integer startYear;
}
