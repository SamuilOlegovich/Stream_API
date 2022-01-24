package stream;


import java.time.LocalDateTime;
import java.io.Serializable;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.*;
import java.util.*;





public class Streams {
    private List<Department> departmentList;
    private List<Employee> employeeList;



    public Streams() {
        createdLists();
    }



    // Создаем стримы
    public void creationStreams() {
        try {
            Stream<String> lines = Files.lines(Paths.get("pom.xml"));
            // получаем стрим путей
            Stream<Path> list = Files.list(Paths.get("./"));
            // получаем стрим путей опредеоенной глубины вложености
            Stream<Path> walk = Files.walk(Paths.get("./"), 3);
        } catch (IOException e) {}

        // стримы для элементарных элементов
        IntStream intStream = IntStream.of(1, 2, 3, 4, 5, 6, 7);
        DoubleStream doubleStream = DoubleStream.of(1.2, 3.4, 8.8, 9,7, 10,11);
        // создаст стрим элементарных чисел - от 10 .. 99
        IntStream range = IntStream.range(10, 100);
        // создаст стрим элементарных чисел - от 10 .. 100
        IntStream rangeTwo = IntStream.rangeClosed(10, 100);

        // создаем архив элементарных и делаем из него стрим
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        IntStream streamArr = Arrays.stream(ints);

        // создаем стрим строк
        Stream<String> stringStream = Stream.of("1", "2", "3");
        Stream<? extends Serializable> stream1 = Stream.of(1, "2", "3", 4, 5, "6", "7");

        // стрим билдер
        Stream<String> streamBuilder = Stream.<String>builder()
                .add("Mike")
                .add("joe")
                .build();

        // делаем стрим из листа
        Stream<Employee> streamEmployee = employeeList.stream();
        // тот же стрим из листа, только паралельный и будет обрабатываться в несколько потоков
        Stream<Employee> parallelStreamEmployee = employeeList.parallelStream();

        // бесконечные стримы (можно так отправлять данные с одного сервиса второму)
        Stream<Event> generate = Stream.generate(() ->
                new Event(UUID.randomUUID(), LocalDateTime.now(), ""));

        // условно бесконечный стрим с шагом в + 3 (передаем начальное значение и лямдовыражение)
        Stream<Integer> iterate = Stream.iterate(1950, val -> val + 3);

        // соединяем два стрима строк
        Stream<String> concat = Stream.concat(stringStream, streamBuilder);
        List<String> strings = concat.collect(Collectors.toList());
        strings.stream().forEach(s -> System.out.println(s));
    }




    // прекращение и вывод стримов
    // (стрим закрывается и для других действий нужно быдет создать новый стрим)
    public void terminateStreams() {
        Stream<Employee> stream = employeeList.stream();
        // отрабатывает и закрывается, для новых действий надо будет создать новый стрим
        stream.count();

        // метод forEach работает на прямую как с коллекциями так и со стримами
        employeeList.stream().forEach(employee -> System.out.println(employee.getAge()));
        employeeList.forEach(employee -> System.out.println(employee.getFirstName()));

        // в отличии от колекций стримы поддерживают метод - forEachOrdered
        // если мы работаем с параллельным стримомм гарантирован весь обход элементов
        employeeList.stream().forEachOrdered(employee -> System.out.println(employee.getPosition()));

        // преобразовываем стрим в любую другую колекцию
        // и не важно изначально из какой колекции был сделан стрим
        employeeList.stream().collect(Collectors.toList());
        employeeList.stream().toArray();

        // ... и даже в меп, единственное надо указать что будет ключем, а что значением
        Map<Integer, String> collect = employeeList.stream().collect(Collectors.toMap(
                emp -> emp.getId(),
                emp -> String.format("%s %s", emp.getLastName(), emp.getFirstName())
        ));

        // делаем  стрим и получаем сумму всех значений
        IntStream intStream = IntStream.of(100, 200, 300, 400);
        int i = intStream.reduce((left, right) -> left + right).orElse(0);
        System.out.println(i);

        // редюсим уже объекты
        // в итоге получаем дерево объектов детей и родителей
        // для прохождения и нахождения детей и родителей пришлось сделать метод - reducer и передать его
        Optional<Department> optionalDepartment = (departmentList.stream().reduce(this::reducer));
        System.out.println(optionalDepartment);

        // для числовых стримов удобно брать статистику
        System.out.println(
                "summaryStatistics " + IntStream.of(100, 200, 300, 400).summaryStatistics());
        System.out.println(
                "Average " + IntStream.of(100, 200, 300, 400).average().orElse(0));
        System.out.println(
                "Max " + IntStream.of(100, 200, 300, 400).max().orElse(0));
        System.out.println(
                "Min " + IntStream.of(100, 200, 300, 400).min().orElse(0));
        System.out.println(
                "Sum " + IntStream.of(100, 200, 300, 400).sum());

        // считаем максимум для списка сотрудников(Объектов)
        // без - get приходит Optional, а так Employee и у него берем - Ageж
        Optional<Employee> employeeOptional = employeeList.stream().max(Comparator.comparingInt(Employee::getAge));
        System.out.println("Max age - " + employeeOptional.toString());


        // в однопоточных стримах разницы нет оба вернут первые элементы
        // в многопоточном же  - findAny вернет произвольный элемен (не обязательно первый)
        // findFirst - вернет первый элемент
        System.out.println(
                employeeList.stream().findAny());
        System.out.println(
                employeeList.stream().findFirst());

        // булевые стримы
        // нет старше 60-ти лет -> true
        System.out.println(
                employeeList.stream().noneMatch(employee -> employee.getAge() > 60));
        // нет младше 18-ти лет -> true
        System.out.println(
                employeeList.stream().allMatch(employee -> employee.getAge() > 18));
        // хотябы один сотрудник является шефом -> true
        System.out.println(
                employeeList.stream().anyMatch(employee -> employee.getPosition() == Position.CHEF));
    }

    public Department reducer(Department parent, Department child) {
        if (child.getParent() == parent.getId()) {
            parent.getChild().add(child);
        } else {
            parent.getChild().forEach(subParent -> reducer(subParent, child));
        }
        return parent;
    }




    // трансформация стримов
    // можем делать что угодно, как хотеть так и трансформировать, стрим не обрывается
    public void transformStreams() {
        // делаем из стрима примитивов стрим состоящий из объектов (int -> Long)
        LongStream longStream = IntStream.of(100, 200, 300, 400).mapToLong(Long::valueOf);

        // делаем из стрими примитивов - стрим объектов (часть информации берем из стрима)
        Stream<Event> eventList = IntStream.of(100, 200, 300, 400).mapToObj(value ->
                new Event(UUID.randomUUID(),
                        LocalDateTime.of(value, 12, 1, 12, 0), "")
        );

        // distinct() - исключает дубликаты, на выходе получим неповторяющиеся числа (100, 200, 300, 400)
        IntStream.of(100, 200, 300, 400, 100, 200).distinct().forEach(x -> System.out.println(x));

        // filter - отфильтровываем объекты в стриме - на выходе получаем всех сотрудников кто не является шефом
        Stream<Employee> employeeStream = employeeList.stream()
                .filter(employee -> employee.getPosition() != Position.CHEF);

        // skip - пропускает n - элементов в начале стрима
        // limit - ограничивает стрим всего n - 'ktvtynfvb
        employeeList.stream()
                .skip(3)
                .limit(5);

        // sorted - сортируем по возрасту сотрудников
        // (методы сортировки указываем в скобках или пишем нужные методы отдельно)
        // peek - ничего не меняет в стриме,
        // но с помощью него можем сменить данные у всех объектов на нужные нам в этом стриме (в данном случаи возраст)
        // map - в итоге получаем стрим строк состоящий из имени и фамилии
        employeeList.stream()
                .sorted(Comparator.comparingInt(Employee::getAge))
                .peek(emp -> emp.setAge(18))
                .map(emp -> String.format("%s %s", emp.getLastName(), emp.getFirstName()))
                .forEach(s -> System.out.println(s));

        // тригер
        // takeWhile - аналог безконечного цикла, можно задавать условия
        // в данном случаи - берет!! сотрудников пока их возраст старше 30 лет,
        // как только условие сработало больше ничего он не возьмет - даже!!
        // если далее по стриму еще будут сотрудники старше 30-ти лет
        employeeList.stream()
                .takeWhile(employee -> employee.getAge() > 30)
                .forEach(System.out::println);

        // тригер
        // dropWhile - тоже что и takeWhile только наоборот
        // он не берет элементы пока условие работает, при первом несрабатывании начинает набирать элементы
        employeeList.stream()
                .dropWhile(employee -> employee.getAge() > 30)
                .forEach(System.out::println);


        // flatMap - расширяем стрим (был стрим с шагом 100, стал с шагом 50)
        IntStream.of(100, 200, 300, 400)
                .flatMap(value -> IntStream.of(value - 50, value))
                .forEach(System.out::println);
    }



    // реальный пример работы стримов с объектами
    public void realStream() {
        // создаем стрим и отфильтровываем рабочих младше 30-ти лет
        // и не были рабочими далее сортируем их по фамилии
        Stream<Employee> stream = employeeList.stream()
                .filter(employee -> employee.getAge() <= 30 && employee.getPosition() != Position.WORKER)
                .sorted(Comparator.comparing(Employee::getLastName));

        printStream(stream);

        // создаем стрим и отфильтровываем рабочих старше 40-ка лет
        // сортируем по возрасту и ограничиваем 4-мя персонами
        Stream<Employee> sorted = employeeList.stream()
                .filter(employee -> employee.getAge() > 40)
                .sorted((o1, o2) -> o2.getAge() - o1.getAge())
                .limit(4);

        printStream(sorted);

        // создаем стрим получаем стрим чисе (лет),
        // а из них сразу вот такую статистику в таком виде
        // IntSummaryStatistics{count=16, sum=600, min=22, average=37,500000, max=54}
        IntSummaryStatistics statistics = employeeList.stream()
                .mapToInt(Employee::getAge)
                .summaryStatistics();

        System.out.println(statistics);
    }

    private void printStream(Stream<Employee> stream) {
        stream.map(emp -> String.format(
                        "%4d | %-15s %-10s age %s %s",
                        emp.getId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getAge(),
                        emp.getPosition()))
                .forEach(System.out::println);
    }




    private void createdLists() {
        this.employeeList = List.of(
                new Employee("Michael", "Smith",   243,  43, Position.CHEF),
                new Employee("Jane",    "Smith",   523,  40, Position.MANAGER),
                new Employee("Jury",    "Gagarin", 6423, 26, Position.MANAGER),
                new Employee("Jack",    "London",  5543, 53, Position.WORKER),
                new Employee("Eric",    "Jackson", 2534, 22,Position.WORKER),
                new Employee("Andrew",  "Bosh",    3456, 44, Position.WORKER),
                new Employee("Joe",     "Smith",   723,  30, Position.MANAGER),
                new Employee("Jack",    "Gagarin", 7423, 35, Position.MANAGER),
                new Employee("Jane",    "London",  7543, 42, Position.WORKER),
                new Employee("Mike",    "Jackson", 7534, 31, Position.WORKER),
                new Employee("Jack",    "Bosh",    7456, 54, Position.WORKER),
                new Employee("Mark",    "Smith",   123,  41, Position.MANAGER),
                new Employee("Jane",    "Gagarin", 1423, 28, Position.MANAGER),
                new Employee("Sam",     "London",  1543, 52, Position.WORKER),
                new Employee("Jack",    "Jackson", 1534, 27, Position.WORKER),
                new Employee("Eric",    "Bosh",    1456, 32, Position.WORKER)
        );

        this.departmentList = List.of(
                new Department(1, 0, "Head"),
                new Department(2, 1, "West"),
                new Department(3, 1, "East"),
                new Department(4, 2, "Germany"),
                new Department(5, 2, "France"),
                new Department(6, 3, "China"),
                new Department(7, 3, "Japan")
        );
    }
}
