package net.devstudy.resume.ms.staticdata.api.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.devstudy.resume.ms.staticdata.api.dto.StaticDataResponse;
import net.devstudy.resume.staticdata.api.model.Hobby;
import net.devstudy.resume.staticdata.api.model.SkillCategory;
import net.devstudy.resume.staticdata.api.service.StaticDataService;
import net.devstudy.resume.shared.model.LanguageLevel;
import net.devstudy.resume.shared.model.LanguageType;

@RestController
@RequestMapping("/api/static-data")
public class StaticDataApiController {

    private final StaticDataService staticDataService;
    private final MessageSource messageSource;

    public StaticDataApiController(
            final StaticDataService staticDataService,
            final MessageSource messageSource
    ) {
        this.staticDataService = staticDataService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public StaticDataResponse staticData() {
        List<StaticDataResponse.SkillCategoryItem> skillCategories = staticDataService.findSkillCategories().stream()
                .filter(Objects::nonNull)
                .map(this::toSkillCategory)
                .toList();
        List<StaticDataResponse.LanguageTypeItem> languageTypes = buildLanguageTypes();
        List<StaticDataResponse.LanguageLevelItem> languageLevels = staticDataService.findAllLanguageLevels().stream()
                .filter(Objects::nonNull)
                .map(this::toLanguageLevel)
                .toList();
        List<StaticDataResponse.HobbyItem> hobbies = staticDataService.findAllHobbies().stream()
                .filter(Objects::nonNull)
                .map(this::toHobby)
                .toList();
        List<StaticDataResponse.MonthItem> months = staticDataService.findMonthMap().entrySet().stream()
                .map(this::toMonth)
                .toList();

        return new StaticDataResponse(
                skillCategories,
                languageTypes,
                languageLevels,
                hobbies,
                staticDataService.findPracticsYears(),
                staticDataService.findCoursesYears(),
                staticDataService.findEducationYears(),
                months
        );
    }

    private StaticDataResponse.SkillCategoryItem toSkillCategory(final SkillCategory category) {
        return new StaticDataResponse.SkillCategoryItem(category.getId(), category.getCategory());
    }

    private StaticDataResponse.HobbyItem toHobby(final Hobby hobby) {
        return new StaticDataResponse.HobbyItem(hobby.getId(), hobby.getName(), hobby.getCssClassName());
    }

    private StaticDataResponse.LanguageLevelItem toLanguageLevel(final LanguageLevel level) {
        return new StaticDataResponse.LanguageLevelItem(level.name(), level.getSliderIntValue());
    }

    private List<StaticDataResponse.LanguageTypeItem> buildLanguageTypes() {
        Locale locale = LocaleContextHolder.getLocale();
        return staticDataService.findAllLanguageTypes().stream()
                .filter(Objects::nonNull)
                .map(type -> toLanguageType(type, locale))
                .toList();
    }

    private StaticDataResponse.LanguageTypeItem toLanguageType(
            final LanguageType type,
            final Locale locale
    ) {
        String code = type.name();
        String label = messageSource.getMessage("language.type." + code, null, code, locale);
        if (label == null || label.isBlank()) {
            label = code;
        }
        return new StaticDataResponse.LanguageTypeItem(code, label);
    }

    private StaticDataResponse.MonthItem toMonth(final Entry<Integer, String> entry) {
        return new StaticDataResponse.MonthItem(entry.getKey(), entry.getValue());
    }
}
