Se desea implementar un sistema de citas en un hospital. Para ello tendremos a los pacientes, donde guardaremos su nombre, dni y dirección. También tendremos al personal sanitario, que pueden ser médicos o enfermeros. De ambos pretendemos guardar también el nombre, dni y número de colegiado. De los médicos guardaremos además sus años de experiencia.
El sistema tiene que abrir automáticamente una ventana de tiempo para poder coger las citas médicas por parte de los pacientes.
Programar backend programado usando Spring Boot que dé soporte a las siguientes operaciones:
•         Alta de paciente.
•         Alta de médic@
•         Alta de enfermer@
•         Configurar horario de un médic@ o enfermer@ (la hora inicio y fin que se indique es aplicable a toda la semana en la que se abre la ventana temporal)
•         Consultar huecos libres de un médic@ o enfermer@. Sólo se podrán obtener los días y horas de la semana siguiente a la fecha actual ya que, como he dicho, es la única ventana temporal con la que opera el sistema.
•         Asignar cita. Tiene que comprobar que, para el paciente, personal sanitario, hora y día indicados, hay hueco libre. No se podrán asignar citas para la semana en curso, sólo a partir del siguiente lunes a la fecha actual, como ya se ha indicado anteriormente.
•         Consultar citas de un paciente para un día determinado, ordenado por horas
•         Consultar las citas que tiene un médic@ para toda la semana, ordenado por días y horas.
•         Consultar los médicos más ocupados (los que tienen más citas) en la próxima ventana temporal. (es decir, entre el lunes de la semana siguiente a hoy, y el viernes)
- La API se tiene que poder documentar usando “Swagger” para Spring boot.
