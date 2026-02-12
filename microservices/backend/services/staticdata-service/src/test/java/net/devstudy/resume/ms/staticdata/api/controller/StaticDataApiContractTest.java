package net.devstudy.resume.ms.staticdata.api.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import net.devstudy.resume.ms.staticdata.StaticDataServiceApplication;
import net.devstudy.resume.shared.model.LanguageLevel;
import net.devstudy.resume.shared.model.LanguageType;
import net.devstudy.resume.staticdata.api.model.Hobby;
import net.devstudy.resume.staticdata.api.model.SkillCategory;
import net.devstudy.resume.staticdata.api.service.StaticDataService;

@Tag("integration")
@SpringBootTest(
        classes = StaticDataServiceApplication.class,
        properties = {
                "app.services.staticdata.mode=remote",
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,"
                        + "org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class StaticDataApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaticDataService staticDataService;

    @Test
    void shouldReturnStableStaticDataContract() throws Exception {
        SkillCategory category = new SkillCategory();
        category.setId(10L);
        category.setCategory("Backend");

        Hobby hobby = new Hobby("Open Source");
        hobby.setId(7L);

        Map<Integer, String> monthMap = new LinkedHashMap<>();
        monthMap.put(1, "January");
        monthMap.put(2, "February");

        given(staticDataService.findSkillCategories()).willReturn(List.of(category));
        given(staticDataService.findAllLanguageTypes()).willReturn(List.of(LanguageType.ALL));
        given(staticDataService.findAllLanguageLevels()).willReturn(List.of(LanguageLevel.BEGINNER));
        given(staticDataService.findAllHobbies()).willReturn(List.of(hobby));
        given(staticDataService.findPracticsYears()).willReturn(List.of(2026, 2025));
        given(staticDataService.findCoursesYears()).willReturn(List.of(2024));
        given(staticDataService.findEducationYears()).willReturn(List.of(2023));
        given(staticDataService.findMonthMap()).willReturn(monthMap);

        mockMvc.perform(get("/api/static-data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", Matchers.hasSize(8)))
                .andExpect(jsonPath("$.skillCategories", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.skillCategories[0].*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.skillCategories[0].id").value(10))
                .andExpect(jsonPath("$.skillCategories[0].category").value("Backend"))
                .andExpect(jsonPath("$.languageTypes", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.languageTypes[0].*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.languageTypes[0].code").value("ALL"))
                .andExpect(jsonPath("$.languageTypes[0].label", Matchers.not(Matchers.isEmptyOrNullString())))
                .andExpect(jsonPath("$.languageLevels", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.languageLevels[0].*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.languageLevels[0].code").value("BEGINNER"))
                .andExpect(jsonPath("$.languageLevels[0].sliderValue")
                        .value(LanguageLevel.BEGINNER.getSliderIntValue()))
                .andExpect(jsonPath("$.hobbies", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.hobbies[0].*", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.hobbies[0].id").value(7))
                .andExpect(jsonPath("$.hobbies[0].name").value("Open Source"))
                .andExpect(jsonPath("$.hobbies[0].cssClassName").value("open-source"))
                .andExpect(jsonPath("$.practicYears", Matchers.contains(2026, 2025)))
                .andExpect(jsonPath("$.courseYears", Matchers.contains(2024)))
                .andExpect(jsonPath("$.educationYears", Matchers.contains(2023)))
                .andExpect(jsonPath("$.months", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.months[0].*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.months[0].value").value(1))
                .andExpect(jsonPath("$.months[0].label").value("January"))
                .andExpect(jsonPath("$.months[1].*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.months[1].value").value(2))
                .andExpect(jsonPath("$.months[1].label").value("February"));
    }
}
