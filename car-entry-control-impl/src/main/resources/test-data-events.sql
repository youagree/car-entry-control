SET search_path TO car_entry_control;

insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (1, 700484, 418346, '2021-07-11 12:33:16', 'OUT', 'Главная проходная', 'ACTIVE', 'А111АА 77', true,
        '{"message": "ошибка сервиса барьер", "statusCode": 500, "erroredServiceName": "барьер"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (777, 619222, 244136, '2020-11-22 21:36:22', 'IN', 'Задняя проходная', 'ACTIVE', 'А222АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (99, 737857, 894864, '2021-06-27 23:44:16', 'OUT', 'Главная проходная', 'UNKNOWN', 'А557АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (13, 770649, 808475, '2021-08-22 04:00:14', 'IN', 'Административный корпус', 'UNKNOWN', 'А557АА 77', false,
        NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (96, 310313, 754637, '2021-04-05 18:34:03', 'IN', 'Главная проходная', 'ACTIVE', 'А222АА 77', false, NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (5, 286092, 893598, '2021-09-29 04:34:05', 'OUT', 'Задняя проходная', 'ACTIVE', 'А727АА 77', false, NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (101, 117293, 963855, '2021-08-21 20:27:11', 'IN', 'Административный корпус', 'ACTIVE', 'А557АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (79, 954726, 624287, '2021-08-23 17:16:30', 'OUT', 'Главная проходная', 'NO_ACTIVE', 'А727АА 77', false, NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (39, 119540, 552406, '2020-11-21 08:17:59', 'OUT', 'Задняя проходная', 'ACTIVE', 'А744АА 77', true,
        '{"message": "ошибка сервиса барьер", "statusCode": 500, "erroredServiceName": "барьер"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (50, 402628, 319689, '2021-04-04 09:36:02', 'IN', 'Административный корпус', 'NO_ACTIVE', 'А737АА 77', false,
        NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (31, 885575, 496745, '2021-04-22 18:51:39', 'IN', 'Задняя проходная', 'ACTIVE', 'А222АА 77', false, NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (100, 934901, 854513, '2021-03-02 18:07:43', 'OUT', 'Главная проходная', 'UNKNOWN', 'А744АА 77', true,
        '{"message": "ошибка сервиса барьер", "statusCode": 500, "erroredServiceName": "барьер"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (37, 840519, 439223, '2020-11-02 17:32:09', 'OUT', 'Главная проходная', 'NO_ACTIVE', 'А727АА 77', false, NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (83, 700815, 17230, '2020-12-04 12:27:49', 'IN', 'Административный корпус', 'ACTIVE', 'А766АА 77', true,
        '{"message": "ошибка сервиса барьер", "statusCode": 500, "erroredServiceName": "барьер"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (40, 777456, 381855, '2021-07-10 10:17:03', 'IN', 'Административный корпус', 'UNKNOWN', 'А557АА 77', false,
        NULL);
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (89, 843369, 539395, '2021-07-22 11:34:59', 'OUT', 'Задняя проходная', 'NO_ACTIVE', 'А557АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (7, 519661, 177618, '2021-07-15 22:10:00', 'IN', 'Административный корпус', 'ACTIVE', 'А744АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
insert into events (event_id, common_id, entry_device_value, event_time, event_type, info, state_of_action, gos_number,
                    is_errored, description)
values (9, 256708, 918827, '2021-09-17 08:37:23', 'IN', 'Административный корпус', 'UNKNOWN', 'А222АА 77', true,
        '{"message": "ошибка рфид", "statusCode": 409, "erroredServiceName": "рфид"}');
