package nogroup.inpaint.image.repository;

import nogroup.inpaint.image.repository.impl.FileRepository;
import nogroup.inpaint.image.repository.impl.InMemoryRepository;
import nogroup.inpaint.image.source.SourceImage;
import nogroup.inpaint.image.source.SourceImageBuilder;
import nogroup.inpaint.image.target.TargetImage;
import nogroup.inpaint.image.target.TargetImageBuilder;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepositoryTest {
    static RepositoryFactory factory;
    static RepositoryImpl impl;
    static Object[] args;

    protected static String sourceImageName(int index) {
        return String.format("source image #%d", index);
    }

    protected static String targetImageName(int sourceIndex, int targetIndex) {
        return String.format("target image #%d of %s", targetIndex, sourceImageName(sourceIndex));
    }

    protected static Supplier<Repository> populatedRepo(int sourcesCount) {
        return populatedRepo(sourcesCount, 0);
    }

    protected static Supplier<Repository> populatedRepo(int sourcesCount, int targetsCount) {
        Map<SourceImage, TargetImage[]> map = new HashMap<>();

        for (int i = 0; i < sourcesCount; i++) {
            SourceImage source = new SourceImageBuilder(sourceImageName(i)).build();
            TargetImage[] targets = new TargetImage[Math.max(0, targetsCount)];
            for (int j = 0; j < targetsCount; j++)
                targets[j] = new TargetImageBuilder(targetImageName(i, j)).build();
            map.put(source, targets);
        }

        return populatedRepo(map);
    }

    protected static Repository expectedRepo(Object[][] rawMap) {
        return populatedRepo(Stream.of(rawMap)
                        .collect(Collectors.toMap(data -> (SourceImage) data[0], data -> (TargetImage[]) data[1])),
                new InMemoryRepository())
                .get();
    }

    protected static Supplier<Repository> populatedRepo(Map<SourceImage, TargetImage[]> map) {
        Repository repository = null;
        try {
            repository = factory.create(impl, args);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return populatedRepo(map, repository);
    }

    protected static Supplier<Repository> populatedRepo(Map<SourceImage, TargetImage[]> map, Repository repository) {
        return () -> {
            if (map == null)
                return repository;
            if (repository instanceof FileRepository) {
                try {
                    ((FileRepository) repository).reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<SourceImage, TargetImage[]> entry : map.entrySet()) {
                if (entry.getKey() == null)
                    continue;

                repository.createSource(entry.getKey());

                if (entry.getValue() == null || entry.getValue().length < 1)
                    continue;

                for (TargetImage target : entry.getValue())
                    repository.createTarget(entry.getKey(), target);
            }

            return repository;
        };
    }


    static void createSource(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final List<SourceImage> sources;
            final List<SourceImage> created;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, List<SourceImage> sources, List<SourceImage> created) {
                super(name, repositorySupplier, throwable, expected);
                this.sources = sources;
                this.created = created;
            }

            @Override
            protected void mainPart() {
                for (int i = 0; i < this.sources.size(); i++) {
                    SourceImage created = this.repository.createSource(this.sources.get(i));
                    Assertions.assertEquals(this.created.get(i), created);
                }
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }
        Testcase[] testcases = new Testcase[]{
                new Testcase("add sources",
                        populatedRepo(0),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{}},
                                {new SourceImageBuilder(sourceImageName(2)).build(), new TargetImage[]{}},
                        }),
                        Arrays.asList(
                                new SourceImageBuilder(sourceImageName(1)).build(),
                                new SourceImageBuilder(sourceImageName(2)).build()
                        ),
                        Arrays.asList(
                                new SourceImageBuilder(sourceImageName(1)).build(),
                                new SourceImageBuilder(sourceImageName(2)).build()
                        )
                ),
                new Testcase("add existing source",
                        populatedRepo(2),
                        new AlreadyExistsException("duplicate source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}},
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{}},
                                {new SourceImageBuilder(sourceImageName(2)).build(), new TargetImage[]{}},
                        }),
                        Arrays.asList(
                                new SourceImageBuilder(sourceImageName(1)).build(),
                                new SourceImageBuilder(sourceImageName(2)).build()
                        ),
                        Arrays.asList(
                                null,
                                new SourceImageBuilder(sourceImageName(2)).build()
                        )
                ),
                new Testcase("add null source",
                        populatedRepo(1),
                        new NullPointerException(),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}},
                        }),
                        Arrays.asList(null, null),
                        Arrays.asList(null, null)
                ),
                new Testcase("add different existing source",
                        populatedRepo(1),
                        new AlreadyExistsException("duplicate source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}},
                        }),
                        Arrays.asList(new SourceImageBuilder(sourceImageName(0)).width(123).build()),
                        Arrays.asList(null, null)
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void createTargets(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final SourceImage source;
            final List<TargetImage> targets;
            final List<TargetImage> created;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, SourceImage source, List<TargetImage> targets, List<TargetImage> created) {
                super(name, repositorySupplier, throwable, expected);
                this.source = source;
                this.targets = targets;
                this.created = created;
            }

            @Override
            protected void mainPart() {
                for (int i = 0; i < this.targets.size(); i++) {
                    TargetImage created = this.repository.createTarget(this.source, this.targets.get(i));
                    Assertions.assertEquals(this.created.get(i), created);
                }
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }
        Testcase[] testcases = new Testcase[]{
                new Testcase("add targets",
                        populatedRepo(1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                                        new TargetImageBuilder(targetImageName(0, 1)).build(),
                                        new TargetImageBuilder(targetImageName(0, 2)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                new TargetImageBuilder(targetImageName(0, 1)).build(),
                                new TargetImageBuilder(targetImageName(0, 2)).build()
                        ),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                new TargetImageBuilder(targetImageName(0, 1)).build(),
                                new TargetImageBuilder(targetImageName(0, 2)).build()
                        )
                ),
                new Testcase("add targets with null source",
                        populatedRepo(1),
                        new NullPointerException(),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}}
                        }),
                        null,
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                new TargetImageBuilder(targetImageName(0, 1)).build(),
                                new TargetImageBuilder(targetImageName(0, 2)).build()
                        ),
                        Arrays.asList(null, null, null)
                ),
                new Testcase("add null targets",
                        populatedRepo(1),
                        new NullPointerException(),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                null
                        ),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                null
                        )
                ),
                new Testcase("add target with existing different one",
                        populatedRepo(1, 1),
                        new AlreadyExistsException("duplicate target nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).height(123).build()
                        ),
                        Arrays.asList(null, null)
                ),
                new Testcase("add target with different existing source",
                        populatedRepo(2, 1),
                        new AlreadyExistsException("target nogroup.inpaint.image assigned to another source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }},
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(1, 0)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(1, 0)).build()
                        ),
                        Arrays.asList(null, null)
                ),
                new Testcase("add target with unknown source",
                        populatedRepo(1, 0),
                        new NotFoundException("unknown source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(1)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(1, 0)).build()
                        ),
                        Arrays.asList(null, null)
                ),
                new Testcase("add existing target",
                        populatedRepo(1, 1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build()
                        ),
                        Arrays.asList(null, null)
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void readSourceByTarget(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final TargetImage target;
            final SourceImage source;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, TargetImage target, SourceImage source) {
                super(name, repositorySupplier, throwable, expected);
                this.target = target;
                this.source = source;
            }

            @Override
            protected void mainPart() {
                SourceImage source = this.repository.readSource(this.target);
                Assertions.assertEquals(this.source, source);
            }

            @Override
            protected void finallyPart() {
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("read source by target",
                        populatedRepo(3, 3),
                        null,
                        null,
                        new TargetImageBuilder(targetImageName(0, 1)).build(),
                        new SourceImageBuilder(sourceImageName(0)).build()
                ),
                new Testcase("read source by unknown target",
                        populatedRepo(3, 3),
                        new NotFoundException("unknown target nogroup.inpaint.image:"),
                        null,
                        new TargetImageBuilder(targetImageName(4, 1)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void readSourceByName(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final String sourceName;
            final SourceImage source;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, String sourceName, SourceImage source) {
                super(name, repositorySupplier, throwable, expected);
                this.sourceName = sourceName;
                this.source = source;
            }

            @Override
            protected void mainPart() {
                SourceImage source = this.repository.readSource(this.sourceName);
                Assertions.assertEquals(this.source, source);
            }

            @Override
            protected void finallyPart() {
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("read source by name",
                        populatedRepo(3, 3),
                        null,
                        null,
                        sourceImageName(0),
                        new SourceImageBuilder(sourceImageName(0)).build()
                ),
                new Testcase("read source by unknown name",
                        populatedRepo(3, 3),
                        new NotFoundException("unknown source nogroup.inpaint.image:"),
                        null,
                        sourceImageName(4),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void readSources(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final List<SourceImage> sources;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, List<SourceImage> sources) {
                super(name, repositorySupplier, throwable, expected);
                this.sources = sources;
            }

            @Override
            protected void mainPart() {
                Set<SourceImage> sources = this.repository.readSources();
                Assertions.assertEquals(this.sources.size(), sources.size());
                for (int i = 0; i < sources.size(); i++) {
                    final int fi = i;
                    Assertions.assertTrue(sources.contains(this.sources.get(i)),
                            () -> String.format("missing %d expected source [%s] in read sources: %s",
                                    fi, this.sources.get(fi), sources));
                }
            }

            @Override
            protected void finallyPart() {
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("read 3 sources",
                        populatedRepo(3),
                        null,
                        null,
                        Arrays.asList(
                                new SourceImageBuilder(sourceImageName(0)).build(),
                                new SourceImageBuilder(sourceImageName(1)).build(),
                                new SourceImageBuilder(sourceImageName(2)).build()
                        )
                ),
                new Testcase("read 0 sources",
                        populatedRepo(0),
                        null,
                        null,
                        Arrays.asList()
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void readTargets(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final SourceImage source;
            final List<TargetImage> targets;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, SourceImage source, List<TargetImage> targets) {
                super(name, repositorySupplier, throwable, expected);
                this.source = source;
                this.targets = targets;
            }

            @Override
            protected void mainPart() {
                List<TargetImage> targets = this.repository.readTargets(this.source);
                Assertions.assertEquals(this.targets.size(), targets.size());
                for (int i = 0; i < targets.size(); i++) {
                    final int fi = i;
                    Assertions.assertEquals(this.targets.get(i), targets.get(i),
                            () -> String.format("wrong %d read target for source [%s]:\n  read\n%s\n  expected\n%s",
                                    fi, this.source, targets.get(fi), this.targets.get(fi)));
                }
            }

            @Override
            protected void finallyPart() {
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("read targets by source",
                        populatedRepo(2, 2),
                        null,
                        null,
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(0, 0)).build(),
                                new TargetImageBuilder(targetImageName(0, 1)).build()
                        )
                ),
                new Testcase("read targets by unknown source",
                        populatedRepo(2, 2),
                        new NotFoundException("unknown source nogroup.inpaint.image:"),
                        null,
                        new SourceImageBuilder(sourceImageName(3)).build(),
                        Arrays.asList(null, null)
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void readTarget(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final String targetName;
            final TargetImage target;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, String targetName, TargetImage target) {
                super(name, repositorySupplier, throwable, expected);
                this.targetName = targetName;
                this.target = target;
            }

            @Override
            protected void mainPart() {
                TargetImage target = this.repository.readTarget(this.targetName);
                Assertions.assertEquals(this.target, target);
            }

            @Override
            protected void finallyPart() {
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("read target by name",
                        populatedRepo(3, 3),
                        null,
                        null,
                        targetImageName(0, 0),
                        new TargetImageBuilder(targetImageName(0, 0)).build()
                ),
                new Testcase("read unknown target by name",
                        populatedRepo(3, 3),
                        new NotFoundException("unknown target nogroup.inpaint.image:"),
                        null,
                        targetImageName(3, 0),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void updateSource(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final SourceImage source;
            final SourceImage updatedSource;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, SourceImage source, SourceImage updatedSource) {
                super(name, repositorySupplier, throwable, expected);
                this.source = source;
                this.updatedSource = updatedSource;
            }

            @Override
            protected void mainPart() {
                SourceImage updatedSource = this.repository.updateSource(this.source);
                Assertions.assertEquals(this.updatedSource, updatedSource);
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("update source",
                        populatedRepo(1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).width(123).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).width(123).build(),
                        new SourceImageBuilder(sourceImageName(0)).width(123).build()
                ),
                new Testcase("update unknown source",
                        populatedRepo(1),
                        new NotFoundException("unknown source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(1)).width(123).build(),
                        null
                ),
                new Testcase("update same source",
                        populatedRepo(1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void updateTarget(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final TargetImage target;
            final TargetImage updatedTarget;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, TargetImage target, TargetImage updatedTarget) {
                super(name, repositorySupplier, throwable, expected);
                this.target = target;
                this.updatedTarget = updatedTarget;
            }

            @Override
            protected void mainPart() {
                TargetImage updatedTarget = this.repository.updateTarget(this.target);
                Assertions.assertEquals(this.updatedTarget, updatedTarget);
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("update target",
                        populatedRepo(1, 1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).height(123).build()
                                }}
                        }),
                        new TargetImageBuilder(targetImageName(0, 0)).height(123).build(),
                        new TargetImageBuilder(targetImageName(0, 0)).height(123).build()
                ),
                new Testcase("update unknown target",
                        populatedRepo(1, 1),
                        new NotFoundException("unknown target nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }}
                        }),
                        new TargetImageBuilder(targetImageName(0, 1)).height(123).build(),
                        null
                ),
                new Testcase("update same target",
                        populatedRepo(1, 1),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build()
                                }}
                        }),
                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void deleteSource(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final SourceImage source;
            final SourceImage deletedSource;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, SourceImage source, SourceImage deletedSource) {
                super(name, repositorySupplier, throwable, expected);
                this.source = source;
                this.deletedSource = deletedSource;
            }

            @Override
            protected void mainPart() {
                SourceImage deletedSource = this.repository.deleteSource(this.source);
                Assertions.assertEquals(this.deletedSource, deletedSource);
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("delete source",
                        populatedRepo(2),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(0)).build(),
                        new SourceImageBuilder(sourceImageName(0)).build()
                ),
                new Testcase("delete unknown source",
                        populatedRepo(2),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{}},
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(2)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void deleteTarget(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final TargetImage target;
            final TargetImage deletedTarget;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, TargetImage target, TargetImage deletedTarget) {
                super(name, repositorySupplier, throwable, expected);
                this.target = target;
                this.deletedTarget = deletedTarget;
            }

            @Override
            protected void mainPart() {
                TargetImage deletedTarget = this.repository.deleteTarget(this.target);
                Assertions.assertEquals(this.deletedTarget, deletedTarget);
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("delete target",
                        populatedRepo(1, 2),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 1)).build()
                                }}
                        }),
                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                        new TargetImageBuilder(targetImageName(0, 0)).build()
                ),
                new Testcase("delete unknown target",
                        populatedRepo(1, 2),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                                        new TargetImageBuilder(targetImageName(0, 1)).build()
                                }}
                        }),
                        new TargetImageBuilder(targetImageName(1, 0)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    static void deleteTargets(RepositoryFactory factory, RepositoryImpl impl, Object... args) {
        RepositoryTest.factory = factory;
        RepositoryTest.impl = impl;
        RepositoryTest.args = args;
        class Testcase extends BaseTestcase {
            final SourceImage source;
            final List<TargetImage> deletedTargets;

            public Testcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected, SourceImage source, List<TargetImage> deletedTargets) {
                super(name, repositorySupplier, throwable, expected);
                this.source = source;
                this.deletedTargets = deletedTargets;
            }

            @Override
            protected void mainPart() {
                List<TargetImage> deletedTargets = this.repository.deleteTargets(this.source);
                Assertions.assertEquals(this.deletedTargets.size(), deletedTargets.size());
                for (int i = 0; i < deletedTargets.size(); i++) {
                    final int fi = i;
                    Assertions.assertEquals(this.deletedTargets.get(i), deletedTargets.get(i),
                            () -> String.format("wrong %d deleted target for source [%s]:\n  deleted\n%s\n  expected\n%s",
                                    fi, this.source, deletedTargets.get(fi), this.deletedTargets.get(fi)));
                }
            }

            @Override
            protected void finallyPart() {
                equalFinallyPart();
            }
        }

        Testcase[] testcases = new Testcase[]{
                new Testcase("delete targets",
                        populatedRepo(2, 2),
                        null,
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                                        new TargetImageBuilder(targetImageName(0, 1)).build()
                                }},
                                {new SourceImageBuilder(sourceImageName(1)).build(), new TargetImage[]{}}
                        }),
                        new SourceImageBuilder(sourceImageName(1)).build(),
                        Arrays.asList(
                                new TargetImageBuilder(targetImageName(1, 0)).build(),
                                new TargetImageBuilder(targetImageName(1, 1)).build()
                        )
                ),
                new Testcase("delete targets of unknown source",
                        populatedRepo(1, 2),
                        new NotFoundException("unknown source nogroup.inpaint.image:"),
                        expectedRepo(new Object[][]{
                                {new SourceImageBuilder(sourceImageName(0)).build(), new TargetImage[]{
                                        new TargetImageBuilder(targetImageName(0, 0)).build(),
                                        new TargetImageBuilder(targetImageName(0, 1)).build()
                                }}
                        }),
                        new SourceImageBuilder(sourceImageName(1)).build(),
                        null
                ),
        };

        for (Testcase testcase : testcases)
            testcase.run();
    }

    protected static abstract class BaseTestcase implements Runnable {
        final String name;
        final Supplier<Repository> repositorySupplier;
        final Throwable throwable;
        final Repository expected;
        Repository repository;

        public BaseTestcase(String name, Supplier<Repository> repositorySupplier, Throwable throwable, Repository expected) {
            this.name = name;
            this.repositorySupplier = repositorySupplier;
            this.throwable = throwable;
            this.expected = expected;
        }

        private void log(String message) {
            System.out.println(message);
        }

        private void logf(String format, Object... args) {
            log(String.format(format, args));
        }

        public void run() {
            logf("run case [%s]", this.name);
            try {
                this.repository = repositorySupplier.get();
                mainPart();
            } catch (Exception e) {
                assertException(e);
            } finally {
                finallyPart();
            }
        }

        protected abstract void mainPart();

        protected abstract void finallyPart();

        protected void equalFinallyPart() {
            final Set<SourceImage> expectedSources = this.expected.readSources();
            final Set<SourceImage> actualSources = this.repository.readSources();
            Assertions.assertEquals(expectedSources.size(), actualSources.size(),
                    () -> String.format("expected and actual sources count mismatch: %d != %d, expected [%s], actual [%s]",
                            expectedSources.size(), actualSources.size(), expectedSources, actualSources));
            Assertions.assertTrue(actualSources.containsAll(expectedSources),
                    () -> String.format("miss expected sources: actual [%s], expected [%s]", actualSources, expectedSources));

            for (SourceImage source : expectedSources) {
                List<TargetImage> expectedTargets = this.expected.readTargets(source);
                List<TargetImage> actualTargets = this.repository.readTargets(source);

                Assertions.assertEquals(expectedTargets.size(), expectedTargets.size(),
                        () -> String.format("expected and actual targets count for source [%s] mismatch: %d != %d",
                                source, expectedSources.size(), actualSources.size()));

                for (int i = 0; i < expectedTargets.size(); i++) {
                    final int fi = i;

                    Assertions.assertEquals(expectedTargets.get(i), actualTargets.get(i),
                            () -> String.format("wrong %d target of source [%s]: expected [%s], actual [%s]",
                                    fi, source, expectedTargets.get(fi), actualTargets.get(fi)));
                }
            }
        }

        private void assertException(Exception exception) {
            Assertions.assertNotNull(this.throwable, () -> "unexpected exception: " + exception.getClass().getCanonicalName());
            Assertions.assertEquals(this.throwable.getClass().getCanonicalName(), exception.getClass().getCanonicalName(),
                    () -> String.format("wrong exception class:\n  exception\n'%s'\n  expected\n'%s'",
                            exception.getClass().getCanonicalName(), this.throwable.getClass().getCanonicalName()));
            if (this.throwable.getMessage() != null) {
                Assertions.assertNotNull(exception.getMessage(), () -> String.format("no message in exception [%s], expected [%s]",
                        exception.getClass().getCanonicalName(), this.throwable.getMessage()));
                Assertions.assertTrue(exception.getMessage().startsWith(this.throwable.getMessage()),
                        () -> String.format("wrong exception message:\n  exception\n'%s'\n  expected\n'%s'",
                                exception.getMessage(), this.throwable.getMessage()));
            }
        }
    }
}
