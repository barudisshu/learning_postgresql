package info.galudisu.pg.integration;

import info.galudisu.pg.entity.Exam;
import info.galudisu.pg.service.IExamService;
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
@DisplayName("Exam service test")
public class ExamTest {

  @Autowired private IExamService examService;

  @DisplayName("_01_: exam has size 4")
  @Test
  void testSelect() {
    List<Exam> examList = examService.list();
    assertThat(examList).hasSize(4);
  }
}
