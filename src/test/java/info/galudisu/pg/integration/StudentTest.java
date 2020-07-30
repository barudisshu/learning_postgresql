package info.galudisu.pg.integration;

import info.galudisu.pg.entity.Student;
import info.galudisu.pg.service.IStudentService;
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
@DisplayName("SERVICE: _03 StudentService")
public class StudentTest {

  @Autowired private IStudentService studentService;

  @DisplayName("_01_: student has size 3")
  @Test
  void testSelect() {
    List<Student> studentList = studentService.list();
    assertThat(studentList).hasSize(3);
  }
}
