package info.galudisu.pg.integration;

import info.galudisu.pg.entity.Course;
import info.galudisu.pg.service.ICourseService;
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
@DisplayName("SERVICE: _01 CourseService")
public class CourseTest {

  @Autowired private ICourseService courseService;

  @DisplayName("_01_: course has size 2")
  @Test
  void testSelect() {
    List<Course> courseList = courseService.list();
    assertThat(courseList).hasSize(2);
  }

  @DisplayName("_02_: course field")
  @Test
  void testField() {
    Course course = courseService.getById("CS301");
    assertThat(course).isNotNull();
    assertThat(course).isNotEqualTo(null);
    assertThat(course.hashCode()).isNotEqualTo(0);
    assertThat(course.getCNo()).isEqualTo("CS301");
    assertThat(course.getHours()).isEqualTo(30);
    assertThat(course.getTitle()).isEqualTo("Databases");
  }
}
