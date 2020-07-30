package info.galudisu.pg.service.impl;

import info.galudisu.pg.entity.Mark;
import info.galudisu.pg.mapper.MarkMapper;
import info.galudisu.pg.service.IMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Galudisu
 * @since 2020-07-30
 */
@Service
public class MarkServiceImpl extends ServiceImpl<MarkMapper, Mark> implements IMarkService {

}
