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
        "Java", "Spring Boot", "Hibernate", "JUnit",
        "Python", "Django",
        "JavaScript", "TypeScript", "Node.js",
        "React", "Next.js", "Vue.js", "Angular", "Tailwind CSS",
        "Go", "Kotlin", "C#", ".NET",
        "SQL", "PostgreSQL", "MySQL", "MongoDB", "Redis",
        "Docker", "Kubernetes",
        "AWS", "Azure", "GCP",
        "Git", "REST API"
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

        saveIfMissing(c, "Desenvolvedor Frontend React/Next.js Pleno", "Mosaic",
            "Construir interfaces modernas com Next.js, React e Tailwind, integrando com APIs REST e otimizando performance.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "7000", "11000",
            "JavaScript", "TypeScript", "React", "Next.js", "Tailwind CSS", "REST API");

        saveIfMissing(c, "Desenvolvedor Python Backend Pleno", "DataLab",
            "Desenvolver microservicos em Python/Django e pipelines de dados com PostgreSQL e Docker.",
            Seniority.MID_LEVEL, WorkMode.HYBRID, "7500", "11000",
            "Python", "Django", "PostgreSQL", "Docker", "Git", "REST API");

        saveIfMissing(c, "Desenvolvedor Fullstack Node.js Pleno", "ConsumerHub",
            "Atuar em aplicacao SaaS com Node.js, TypeScript, React e MongoDB; cultura de testes e code review.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "7000", "10000",
            "JavaScript", "TypeScript", "Node.js", "React", "MongoDB", "REST API");

        saveIfMissing(c, "DevOps/SRE Senior", "SkyOps",
            "Definir e operar plataforma Kubernetes em AWS, automacao de CI/CD, observabilidade e cost optimization.",
            Seniority.SENIOR, WorkMode.REMOTE, "14000", "20000",
            "Docker", "Kubernetes", "AWS", "Git", "PostgreSQL");

        saveIfMissing(c, "Desenvolvedor Vue.js Pleno", "FrontHub",
            "Construir SPAs em Vue.js 3 + TypeScript com Tailwind CSS, integrando com APIs REST de um produto B2B.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "6500", "9500",
            "JavaScript", "TypeScript", "Vue.js", "Tailwind CSS", "REST API");

        saveIfMissing(c, "Desenvolvedor Angular Senior", "EnterpriseSoft",
            "Liderar evolucao de aplicacao corporativa em Angular + TypeScript, com padroes de arquitetura e testes.",
            Seniority.SENIOR, WorkMode.HYBRID, "12000", "17000",
            "JavaScript", "TypeScript", "Angular", "REST API", "Git");

        saveIfMissing(c, "Desenvolvedor Go Backend Pleno", "StreamLab",
            "Construir servicos de alta concorrencia em Go com PostgreSQL e Docker para plataforma de streaming.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "8500", "12500",
            "Go", "PostgreSQL", "Docker", "REST API", "Git");

        saveIfMissing(c, "Desenvolvedor .NET Senior", "NetSquare",
            "Modernizar plataforma corporativa em C# / .NET 8, com SQL Server e deploy no Azure.",
            Seniority.SENIOR, WorkMode.HYBRID, "13000", "18000",
            "C#", ".NET", "SQL", "Azure", "Git");

        saveIfMissing(c, "Desenvolvedor Backend Java/MySQL Junior", "LegacyHub",
            "Apoiar evolucao de sistema legado em Java/Spring Boot com MySQL e migracao gradual de queries Hibernate.",
            Seniority.JUNIOR, WorkMode.ONSITE, "4000", "6000",
            "Java", "Spring Boot", "MySQL", "Hibernate", "REST API");

        saveIfMissing(c, "Desenvolvedor Fullstack TypeScript Pleno", "RealTimeCo",
            "Aplicacao realtime em Node.js + React/TypeScript, com cache distribuido em Redis e dados em MongoDB.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "7500", "11000",
            "TypeScript", "Node.js", "React", "MongoDB", "Redis", "REST API");

        saveIfMissing(c, "Engenheiro Cloud GCP Senior", "CloudNative",
            "Definir e operar plataforma em GCP usando Kubernetes (GKE), services em Go e PostgreSQL gerenciado.",
            Seniority.SENIOR, WorkMode.REMOTE, "14000", "20000",
            "GCP", "Kubernetes", "Docker", "Go", "PostgreSQL");

        saveIfMissing(c, "Desenvolvedor Kotlin Backend Pleno", "JvmTech",
            "Construir microservicos em Kotlin + Spring Boot com PostgreSQL e Docker, aproveitando recursos modernos da JVM.",
            Seniority.MID_LEVEL, WorkMode.REMOTE, "8000", "12000",
            "Kotlin", "Spring Boot", "PostgreSQL", "Hibernate", "REST API", "Docker");

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
