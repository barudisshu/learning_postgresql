package info.galudisu.pg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import info.galudisu.pg.entity.Student;

/**
 * Mapper 接口
 *
 * @author Galudisu
 * @since 2020-07-29
 */
public interface StudentMapper extends BaseMapper<Student> {
  IPage<Student> selectPageVo(Page<?> page, Integer startYear);
}
