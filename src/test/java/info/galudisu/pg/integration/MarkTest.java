package info.galudisu.pg.integration;

import info.galudisu.pg.entity.Mark;
import info.galudisu.pg.service.IMarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(value = {SpringExtension.class})
@SpringBootTest
@DisplayName("SERVICE: _04 MarkService")
public class MarkTest {

  @Autowired private IMarkService markService;

  @DisplayName("_01_: mark has size 3")
  @Test
  void testSelect() {
    List<Mark> markList = markService.list();
    assertThat(markList).hasSize(3);
    assertThat(markList.get(0)).isNotNull();
    assertThat(markList.get(0)).isNotEqualTo(null);
    assertThat(markList.get(0).hashCode()).isNotEqualTo(0);
    assertThat(markList.get(0).getScore()).isNotEqualTo(0);
  }
}
