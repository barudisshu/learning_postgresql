package info.galudisu.pg.service.impl;

import info.galudisu.pg.entity.Exam;
import info.galudisu.pg.mapper.ExamMapper;
import info.galudisu.pg.service.IExamService;
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
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {

}
