package com.wiam.lms.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.wiam.lms.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.wiam.lms.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.wiam.lms.domain.User.class.getName());
            createCache(cm, com.wiam.lms.domain.Authority.class.getName());
            createCache(cm, com.wiam.lms.domain.User.class.getName() + ".authorities");
            createCache(cm, com.wiam.lms.domain.UserCustom.class.getName());
            createCache(cm, com.wiam.lms.domain.UserCustom.class.getName() + ".languages");
            createCache(cm, com.wiam.lms.domain.UserCustom.class.getName() + ".exams");
            createCache(cm, com.wiam.lms.domain.Job.class.getName());
            createCache(cm, com.wiam.lms.domain.Job.class.getName() + ".userCustoms");
            createCache(cm, com.wiam.lms.domain.Country2.class.getName());
            createCache(cm, com.wiam.lms.domain.Language.class.getName());
            createCache(cm, com.wiam.lms.domain.Departement.class.getName());
            createCache(cm, com.wiam.lms.domain.Departement.class.getName() + ".employees");
            createCache(cm, com.wiam.lms.domain.Departement.class.getName() + ".departements");
            createCache(cm, com.wiam.lms.domain.Level.class.getName());
            createCache(cm, com.wiam.lms.domain.Level.class.getName() + ".courses");
            createCache(cm, com.wiam.lms.domain.Topic.class.getName());
            createCache(cm, com.wiam.lms.domain.Topic.class.getName() + ".courses");
            createCache(cm, com.wiam.lms.domain.Topic.class.getName() + ".topics");
            createCache(cm, com.wiam.lms.domain.Site.class.getName());
            createCache(cm, com.wiam.lms.domain.Site.class.getName() + ".classrooms");
            createCache(cm, com.wiam.lms.domain.City.class.getName());
            createCache(cm, com.wiam.lms.domain.City.class.getName() + ".sites");
            createCache(cm, com.wiam.lms.domain.Country.class.getName());
            createCache(cm, com.wiam.lms.domain.Country.class.getName() + ".students");
            createCache(cm, com.wiam.lms.domain.Country.class.getName() + ".userCustoms");
            createCache(cm, com.wiam.lms.domain.Classroom.class.getName());
            createCache(cm, com.wiam.lms.domain.Classroom.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Group.class.getName());
            createCache(cm, com.wiam.lms.domain.Group.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Group.class.getName() + ".students");
            createCache(cm, com.wiam.lms.domain.Group.class.getName() + ".groups");
            createCache(cm, com.wiam.lms.domain.JobTitle.class.getName());
            createCache(cm, com.wiam.lms.domain.JobTitle.class.getName() + ".employees");
            createCache(cm, com.wiam.lms.domain.Sponsor.class.getName());
            createCache(cm, com.wiam.lms.domain.Sponsor.class.getName() + ".sponsorings");
            createCache(cm, com.wiam.lms.domain.Sponsor.class.getName() + ".students");
            createCache(cm, com.wiam.lms.domain.Currency.class.getName());
            createCache(cm, com.wiam.lms.domain.Currency.class.getName() + ".sponsorings");
            createCache(cm, com.wiam.lms.domain.Currency.class.getName() + ".payments");
            createCache(cm, com.wiam.lms.domain.Employee.class.getName());
            createCache(cm, com.wiam.lms.domain.Employee.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Professor.class.getName());
            createCache(cm, com.wiam.lms.domain.Professor.class.getName() + ".courses");
            createCache(cm, com.wiam.lms.domain.Professor.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Student.class.getName());
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".coteryHistories");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".certificates");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".enrolements");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".answers");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".progressions");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".sponsors");
            createCache(cm, com.wiam.lms.domain.Student.class.getName() + ".quizCertificates");
            createCache(cm, com.wiam.lms.domain.DiplomaType.class.getName());
            createCache(cm, com.wiam.lms.domain.DiplomaType.class.getName() + ".diplomas");
            createCache(cm, com.wiam.lms.domain.Diploma.class.getName());
            createCache(cm, com.wiam.lms.domain.Enrolement.class.getName());
            createCache(cm, com.wiam.lms.domain.Enrolement.class.getName() + ".payments");
            createCache(cm, com.wiam.lms.domain.Course.class.getName());
            createCache(cm, com.wiam.lms.domain.Course.class.getName() + ".parts");
            createCache(cm, com.wiam.lms.domain.Course.class.getName() + ".enrolements");
            createCache(cm, com.wiam.lms.domain.Course.class.getName() + ".questions");
            createCache(cm, com.wiam.lms.domain.Part.class.getName());
            createCache(cm, com.wiam.lms.domain.Part.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Part.class.getName() + ".reviews");
            createCache(cm, com.wiam.lms.domain.Part.class.getName() + ".parts");
            createCache(cm, com.wiam.lms.domain.Part.class.getName() + ".quizCertificates");
            createCache(cm, com.wiam.lms.domain.Review.class.getName());
            createCache(cm, com.wiam.lms.domain.QuizCertificate.class.getName());
            createCache(cm, com.wiam.lms.domain.QuizCertificate.class.getName() + ".students");
            createCache(cm, com.wiam.lms.domain.QuizCertificate.class.getName() + ".questions");
            createCache(cm, com.wiam.lms.domain.QuizCertificateType.class.getName());
            createCache(cm, com.wiam.lms.domain.QuizCertificateType.class.getName() + ".quizCertificates");
            createCache(cm, com.wiam.lms.domain.Question.class.getName());
            createCache(cm, com.wiam.lms.domain.Question.class.getName() + ".answers");
            createCache(cm, com.wiam.lms.domain.Question.class.getName() + ".quizzes");
            createCache(cm, com.wiam.lms.domain.Question.class.getName() + ".quizCertificates");
            createCache(cm, com.wiam.lms.domain.Answer.class.getName());
            createCache(cm, com.wiam.lms.domain.Progression.class.getName());
            createCache(cm, com.wiam.lms.domain.ProgressionMode.class.getName());
            createCache(cm, com.wiam.lms.domain.ProgressionMode.class.getName() + ".progressions");
            createCache(cm, com.wiam.lms.domain.Payment.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionType.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionType.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.SessionMode.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionMode.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.SessionJoinMode.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionJoinMode.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.SessionProvider.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionProvider.class.getName() + ".sessionLinks");
            createCache(cm, com.wiam.lms.domain.SessionLink.class.getName());
            createCache(cm, com.wiam.lms.domain.SessionLink.class.getName() + ".sessions");
            createCache(cm, com.wiam.lms.domain.Session.class.getName());
            createCache(cm, com.wiam.lms.domain.Session.class.getName() + ".progression1s");
            createCache(cm, com.wiam.lms.domain.Session.class.getName() + ".quizCertificates");
            createCache(cm, com.wiam.lms.domain.Session.class.getName() + ".professors");
            createCache(cm, com.wiam.lms.domain.Session.class.getName() + ".employees");
            createCache(cm, com.wiam.lms.domain.Session.class.getName() + ".links");
            createCache(cm, com.wiam.lms.domain.Tickets.class.getName());
            createCache(cm, com.wiam.lms.domain.TicketSubjects.class.getName());
            createCache(cm, com.wiam.lms.domain.TicketSubjects.class.getName() + ".tickets");
            createCache(cm, com.wiam.lms.domain.Project.class.getName());
            createCache(cm, com.wiam.lms.domain.Project.class.getName() + ".sponsorings");
            createCache(cm, com.wiam.lms.domain.Sponsoring.class.getName());
            createCache(cm, com.wiam.lms.domain.Quiz.class.getName());
            createCache(cm, com.wiam.lms.domain.Quiz.class.getName() + ".questions");
            createCache(cm, com.wiam.lms.domain.Question2.class.getName());
            createCache(cm, com.wiam.lms.domain.Certificate.class.getName());
            createCache(cm, com.wiam.lms.domain.CoteryHistory.class.getName());
            createCache(cm, com.wiam.lms.domain.Cotery.class.getName());
            createCache(cm, com.wiam.lms.domain.Cotery.class.getName() + ".coteryHistories");
            createCache(cm, com.wiam.lms.domain.Cotery.class.getName() + ".certificates");
            createCache(cm, com.wiam.lms.domain.FollowUp.class.getName());
            createCache(cm, com.wiam.lms.domain.LeaveRequest.class.getName());
            createCache(cm, com.wiam.lms.domain.Exam.class.getName());
            createCache(cm, com.wiam.lms.domain.Exam.class.getName() + ".userCustoms");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
