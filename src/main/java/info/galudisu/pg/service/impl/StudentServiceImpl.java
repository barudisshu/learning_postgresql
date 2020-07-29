package info.galudisu.pg.service.impl;

import info.galudisu.pg.entity.Student;
import info.galudisu.pg.mapper.StudentMapper;
import info.galudisu.pg.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Galudisu
 * @since 2020-07-29
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

}
