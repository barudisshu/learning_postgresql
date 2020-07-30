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
@DisplayName("SERVICE: _02 ExamService")
public class ExamTest {

  @Autowired private IExamService examService;

  @DisplayName("_01_: exam has size 4")
  @Test
  void testSelect() {
    List<Exam> examList = examService.list();
    assertThat(examList).hasSize(4);
  }

  @DisplayName("_02_: exam field")
  @Test
  void testField() {
    Exam exam = examService.getById(1556);
    assertThat(exam).isNotNull();
    assertThat(exam).isNotEqualTo(null);
    assertThat(exam.hashCode()).isNotEqualTo(0);
    assertThat(exam.getSId()).isEqualTo(1556);
    assertThat(exam.getCNo()).isEqualTo("CS301");
    assertThat(exam.getScore()).isEqualTo(5);
  }
}
