package info.galudisu.pg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Galudisu
 * @since 2020-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mark")
public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("m_id")
    private String mId;

    @TableField("score")
    private Integer score;


}
