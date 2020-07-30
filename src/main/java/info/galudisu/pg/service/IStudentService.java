package info.galudisu.pg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import info.galudisu.pg.entity.Student;

/**
 * 服务类
 *
 * @author Galudisu
 * @since 2020-07-29
 */
public interface IStudentService extends IService<Student> {

  IPage<Student> selectStudentPage(Page<Student> page, Integer startYear);
}
