package info.galudisu.pg.service.impl;

import info.galudisu.pg.entity.Course;
import info.galudisu.pg.mapper.CourseMapper;
import info.galudisu.pg.service.ICourseService;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}
