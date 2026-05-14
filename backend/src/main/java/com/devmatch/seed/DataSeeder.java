package com.devmatch.seed;

import com.devmatch.job.Job;
import com.devmatch.job.JobRepository;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.Skill;
import com.devmatch.skill.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private static final List<String> CATALOG = List.of(
        "Java", "Spring Boot", "SQL", "PostgreSQL", "React",
        "Docker", "Git", "AWS", "Hibernate", "JUnit"
    );

    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Skill> skills = seedSkills();
        int inserted = seedJobs(skills);
        if (inserted > 0) {
            log.info("DataSeeder: inserted {} new job(s); total skills={}, total jobs={}",
                inserted, skills.size(), jobRepository.count());
        } else {
            log.info("DataSeeder: catalog up to date ({} skills, {} jobs)",
                skills.size(), jobRepository.count());
        }
    }

    private Map<String, Skill> seedSkills() {
        Map<String, Skill> result = new HashMap<>();
        for (String name : CATALOG) {
            Skill skill = skillRepository.findByName(name)
                .orElseGet(() -> skillRepository.save(new Skill(name)));
            result.put(name, skill);
        }
        return result;
    }

    private int seedJobs(Map<String, Skill> c) {
        int before = (int) jobRepository.count();

        saveIfMissing(c, "Desenvolvedor Java Junior", "Tech Solutions",
            "Atuar no desenvolvimento de APIs REST em Java/Spring Boot, com foco em manutencao evolutiva e correcao de bugs.",
            Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot", "SQL", "Git");

        saveIfMissing(c, "Desenvolvedor Backend Java Pleno", "FinPay",
            "Construir microservicos em Spring Boot com PostgreSQL e Docker para a plataforma de pagamentos.",
            Seniority.MID_LEVEL, WorkMode.HYBRID, "7000", "10000",
            "Java", "Spring Boot", "SQL", "PostgreSQL", "Docker", "Hibernate");

        saveIfMissing(c, "Desenvolvedor Fullstack Java/React", "Insightly",
            "Desenvolver features end-to-end usando Java/Spring no backend e React/TypeScript no frontend.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "6000", "9000",
            "Java", "Spring Boot", "React", "SQL", "Git");

        saveIfMissing(c, "Estagiario Backend Java", "DataCorp",
            "Apoiar a equipe de backend em tarefas iniciais de desenvolvimento, testes e documentacao.",
            Seniority.INTERN, WorkMode.ONSITE, "1500", "2500",
            "Java", "Git");

        saveIfMissing(c, "Desenvolvedor Java Spring Boot Senior", "CloudWorks",
            "Liderar tecnicamente o desenvolvimento de servicos cloud-native em AWS com Java 21 e Spring Boot 3.",
            Seniority.SENIOR, WorkMode.REMOTE, "12000", "16000",
            "Java", "Spring Boot", "PostgreSQL", "Docker", "AWS", "Hibernate", "JUnit");

        saveIfMissing(c, "Tech Lead Backend Java", "Bravo Bank",
            "Liderar squad backend, definir padroes de arquitetura, mentorar devs e revisar entregas em Spring Boot.",
            Seniority.SENIOR, WorkMode.HYBRID, "15000", "22000",
            "Java", "Spring Boot", "PostgreSQL", "Docker", "AWS", "Hibernate", "JUnit");

        saveIfMissing(c, "DevOps Engineer Pleno", "CloudWorks",
            "Manter pipelines CI/CD, infraestrutura AWS, observabilidade e automacao de deploys de servicos Java.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "8000", "12000",
            "Docker", "AWS", "Git", "PostgreSQL");

        saveIfMissing(c, "Desenvolvedor Backend Pleno", "RetailTech",
            "Evoluir o monolito Java/Spring de e-commerce, melhorando performance, qualidade de codigo e cobertura de testes.",
            Seniority.MID_LEVEL, WorkMode.ONSITE, "6500", "9000",
            "Java", "Spring Boot", "SQL", "Hibernate", "Git");

        saveIfMissing(c, "Desenvolvedor Backend Junior", "StartUpX",
            "Trabalhar em um produto SaaS Java com forte cultura de testes, code review e deploy continuo em containers.",
            Seniority.JUNIOR, WorkMode.REMOTE, "4500", "6500",
            "Java", "Spring Boot", "PostgreSQL", "Git", "Docker");

        saveIfMissing(c, "Engenheiro de Software Senior", "NovaTech",
            "Construir produtos fullstack escalaveis com Spring Boot, React e PostgreSQL em arquitetura modular.",
            Seniority.SENIOR, WorkMode.REMOTE, "13000", "17000",
            "Java", "Spring Boot", "React", "PostgreSQL", "Docker", "JUnit");

        saveIfMissing(c, "Desenvolvedor Junior React/Java", "WebStudio",
            "Implementar telas em React/TypeScript e endpoints simples em Java/Spring para clientes de marketing digital.",
            Seniority.JUNIOR, WorkMode.HYBRID, "4000", "6000",
            "Java", "React", "Git", "SQL");

        saveIfMissing(c, "Estagiario Fullstack", "GreenSoft",
            "Apoiar a equipe fullstack em pequenas tarefas de Java, React, testes manuais e documentacao tecnica.",
            Seniority.INTERN, WorkMode.ONSITE, "1800", "2800",
            "Java", "React", "Git");

        return (int) jobRepository.count() - before;
    }

    private void saveIfMissing(Map<String, Skill> catalog,
                                String title, String company, String description,
                                Seniority seniority, WorkMode workMode,
                                String minSalary, String maxSalary,
                                String... requiredSkills) {
        if (jobRepository.findByTitle(title).isPresent()) {
            return;
        }
        Job job = new Job();
        job.setTitle(title);
        job.setCompany(company);
        job.setDescription(description);
        job.setSeniority(seniority);
        job.setWorkMode(workMode);
        job.setMinSalary(new BigDecimal(minSalary));
        job.setMaxSalary(new BigDecimal(maxSalary));

        Set<Skill> jobSkills = new LinkedHashSet<>();
        for (String name : requiredSkills) {
            Skill s = catalog.get(name);
            if (s == null) {
                throw new IllegalStateException("Skill not found in catalog: " + name);
            }
            jobSkills.add(s);
        }
        job.setSkills(jobSkills);
        jobRepository.save(job);
    }
}
