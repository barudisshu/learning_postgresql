package info.galudisu.pg.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.galudisu.pg.entity.Student;
import info.galudisu.pg.mapper.StudentMapper;
import info.galudisu.pg.service.IStudentService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author Galudisu
 * @since 2020-07-29
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements IStudentService {

  public IPage<Student> selectStudentPage(Page<Student> page, Integer startYear) {
    return baseMapper.selectPageVo(page, startYear);
  }
}
