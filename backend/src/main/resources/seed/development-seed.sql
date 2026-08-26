-- MySQL 8 local. Confirme antes: SELECT DATABASE(); deve retornar concessionaria.
-- Execução exclusivamente manual. Não use --force nem execute partes isoladas.
SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS concessionaria.sp_development_seed_20260826;
DELIMITER $$

CREATE PROCEDURE concessionaria.sp_development_seed_20260826()
seed: BEGIN
    DECLARE v_usuarios JSON;
    DECLARE v_clientes JSON;
    DECLARE v_veiculos JSON;
    DECLARE v_vendas JSON;
    DECLARE v_user_overlap INT DEFAULT 0;
    DECLARE v_user_exact INT DEFAULT 0;
    DECLARE v_cliente_overlap INT DEFAULT 0;
    DECLARE v_cliente_exact INT DEFAULT 0;
    DECLARE v_veiculo_exact INT DEFAULT 0;
    DECLARE v_venda_exact INT DEFAULT 0;
    DECLARE v_error_message TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1 v_error_message = MESSAGE_TEXT;
        ROLLBACK;
        SELECT 'ABORTADO POR ERRO' AS resultado,
               CONCAT('Nenhum dado foi confirmado. Motivo: ', v_error_message) AS mensagem;
    END;

    IF DATABASE() IS NULL OR DATABASE() <> 'concessionaria' THEN
        SELECT 'ABORTADO POR SEGURANÇA' AS resultado,
               CONCAT('Banco selecionado: ', COALESCE(DATABASE(), '<nenhum>'),
                      '. Selecione concessionaria e execute novamente.') AS mensagem;
        LEAVE seed;
    END IF;

    SET v_usuarios = JSON_ARRAY(
        JSON_OBJECT('nome','Administrador de Demonstração','email','admin.demo@sistemadegestao.local','senha','$2a$10$wWx1WEra0fneX.DrC2OkSuiOvmxNneB4ytQLJ8tymjK40fsx04irS','role','ADMIN'),
        JSON_OBJECT('nome','Usuário de Demonstração','email','usuario.demo@sistemadegestao.local','senha','$2a$10$lZlV0IeZ6NsuQAfSlxnqj.0Mbm0Jyns5SaT19D5IBkkYXgHlVKhDy','role','USER')
    );

    SET v_clientes = JSON_ARRAY(
        JSON_OBJECT('ref',1,'nome','Ana Almeida Ferreira','cpf','12345000198','telefone','8199100001','email','cliente01@example.com'),
        JSON_OBJECT('ref',2,'nome','Beatriz Costa Oliveira','cpf','12345000279','telefone','8199100002','email','cliente02@example.com'),
        JSON_OBJECT('ref',3,'nome','Bruno Gomes Souza','cpf','12345000350','telefone','8199100003','email','cliente03@example.com'),
        JSON_OBJECT('ref',4,'nome','Caio Melo Duarte','cpf','12345000430','telefone','8199100004','email','cliente04@example.com'),
        JSON_OBJECT('ref',5,'nome','Camila Oliveira Ramos','cpf','12345000511','telefone','8199100005','email','cliente05@example.com'),
        JSON_OBJECT('ref',6,'nome','Carla Rocha Dias','cpf','12345000600','telefone','8199100006','email','cliente06@example.com'),
        JSON_OBJECT('ref',7,'nome','Carlos Silva Nascimento','cpf','12345000782','telefone','8199100007','email','cliente07@example.com'),
        JSON_OBJECT('ref',8,'nome','Daniel Vieira Silva','cpf','12345000863','telefone','8199100008','email','cliente08@example.com'),
        JSON_OBJECT('ref',9,'nome','Eduarda Cavalcanti Correia','cpf','12345000944','telefone','8199100009','email','cliente09@example.com'),
        JSON_OBJECT('ref',10,'nome','Felipe Freitas Queiroz','cpf','12345001088','telefone','8199100010','email','cliente10@example.com'),
        JSON_OBJECT('ref',11,'nome','Fernanda Nunes Costa','cpf','12345001169','telefone','8199100011','email','cliente11@example.com'),
        JSON_OBJECT('ref',12,'nome','Gabriel Ramos Monteiro','cpf','12345001240','telefone','8199100012','email','cliente12@example.com'),
        JSON_OBJECT('ref',13,'nome','Helena Almeida Santos','cpf','12345001320','telefone','8199100013','email','cliente13@example.com'),
        JSON_OBJECT('ref',14,'nome','Igor Costa Cavalcanti','cpf','12345001401','telefone','8199100014','email','cliente14@example.com'),
        JSON_OBJECT('ref',15,'nome','Isabela Gomes Pinto','cpf','12345001592','telefone','8199100015','email','cliente15@example.com'),
        JSON_OBJECT('ref',16,'nome','Joana Melo Cardoso','cpf','12345001673','telefone','8199100016','email','cliente16@example.com'),
        JSON_OBJECT('ref',17,'nome','João Oliveira Melo','cpf','12345001754','telefone','8199100017','email','cliente17@example.com'),
        JSON_OBJECT('ref',18,'nome','Juliana Rocha Santana','cpf','12345001835','telefone','8199100018','email','cliente18@example.com'),
        JSON_OBJECT('ref',19,'nome','Larissa Silva Campos','cpf','12345001916','telefone','8199100019','email','cliente19@example.com'),
        JSON_OBJECT('ref',20,'nome','Leonardo Vieira Nunes','cpf','12345002050','telefone','8199100020','email','cliente20@example.com')
    );

    SET v_veiculos = JSON_ARRAY(
        JSON_OBJECT('ref','nota-1','marca','Chevrolet','modelo','Onix','ano',2025,'cor','Branco','km',NULL,'preco',94990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-2','marca','Hyundai','modelo','HB20','ano',2025,'cor','Prata','km',NULL,'preco',89990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-3','marca','Volkswagen','modelo','Polo','ano',2025,'cor','Cinza','km',NULL,'preco',99990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-4','marca','Peugeot','modelo','208','ano',2025,'cor','Preto','km',NULL,'preco',104990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-5','marca','Fiat','modelo','Argo','ano',2025,'cor','Vermelho','km',NULL,'preco',92990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-6','marca','Renault','modelo','Kwid','ano',2025,'cor','Branco','km',NULL,'preco',78990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-7','marca','Toyota','modelo','Corolla','ano',2025,'cor','Prata','km',NULL,'preco',169990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-8','marca','Honda','modelo','Civic','ano',2026,'cor','Preto','km',NULL,'preco',265900.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-9','marca','Volkswagen','modelo','Virtus','ano',2026,'cor','Azul','km',NULL,'preco',139990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-10','marca','Nissan','modelo','Sentra','ano',2026,'cor','Cinza','km',NULL,'preco',179990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-11','marca','Volkswagen','modelo','T-Cross','ano',2026,'cor','Branco','km',NULL,'preco',189990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-12','marca','Hyundai','modelo','Creta','ano',2026,'cor','Prata','km',NULL,'preco',184990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-13','marca','Chevrolet','modelo','Tracker','ano',2026,'cor','Azul','km',NULL,'preco',174990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-14','marca','Jeep','modelo','Compass','ano',2026,'cor','Preto','km',NULL,'preco',229990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','nota-15','marca','Toyota','modelo','Corolla Cross','ano',2026,'cor','Branco','km',NULL,'preco',209990.00,'status','VENDIDO'),
        JSON_OBJECT('ref','novo-27','marca','Porsche','modelo','Cayenne','ano',2026,'cor','Cinza','km',NULL,'preco',995000.00,'status','DISPONIVEL'),
        JSON_OBJECT('ref','novo-29','marca','Ferrari','modelo','Roma','ano',2026,'cor','Vermelho','km',NULL,'preco',3400000.00,'status','DISPONIVEL'),
        JSON_OBJECT('ref','usado-26','marca','Mitsubishi','modelo','Lancer','ano',2018,'cor','Branco','km',73100,'preco',99990.00,'status','DISPONIVEL'),
        JSON_OBJECT('ref','usado-28','marca','Ford','modelo','Ranger','ano',2021,'cor','Prata','km',68800,'preco',239990.00,'status','DISPONIVEL'),
        JSON_OBJECT('ref','usado-30','marca','Nissan','modelo','Frontier','ano',2022,'cor','Cinza','km',57900,'preco',219990.00,'status','DISPONIVEL')
    );

    SET v_vendas = JSON_ARRAY(
        JSON_OBJECT('nota',1,'cliente',1,'veiculo','nota-1','valor',98190.00,'data','2026-01-08'),
        JSON_OBJECT('nota',2,'cliente',2,'veiculo','nota-2','valor',90840.00,'data','2026-01-12'),
        JSON_OBJECT('nota',3,'cliente',3,'veiculo','nota-3','valor',103490.00,'data','2026-01-17'),
        JSON_OBJECT('nota',4,'cliente',4,'veiculo','nota-4','valor',105890.00,'data','2026-01-23'),
        JSON_OBJECT('nota',5,'cliente',5,'veiculo','nota-5','valor',93440.00,'data','2026-01-29'),
        JSON_OBJECT('nota',6,'cliente',6,'veiculo','nota-6','valor',80440.00,'data','2026-02-03'),
        JSON_OBJECT('nota',7,'cliente',7,'veiculo','nota-7','valor',176190.00,'data','2026-02-08'),
        JSON_OBJECT('nota',8,'cliente',8,'veiculo','nota-8','valor',270600.00,'data','2026-02-14'),
        JSON_OBJECT('nota',9,'cliente',9,'veiculo','nota-9','valor',142590.00,'data','2026-02-20'),
        JSON_OBJECT('nota',10,'cliente',10,'veiculo','nota-10','valor',181890.00,'data','2026-02-26'),
        JSON_OBJECT('nota',11,'cliente',11,'veiculo','nota-11','valor',192290.00,'data','2026-03-03'),
        JSON_OBJECT('nota',12,'cliente',12,'veiculo','nota-12','valor',189190.00,'data','2026-03-09'),
        JSON_OBJECT('nota',13,'cliente',13,'veiculo','nota-13','valor',177390.00,'data','2026-03-15'),
        JSON_OBJECT('nota',14,'cliente',14,'veiculo','nota-14','valor',232890.00,'data','2026-03-21'),
        JSON_OBJECT('nota',15,'cliente',15,'veiculo','nota-15','valor',211790.00,'data','2026-03-27')
    );

    SELECT COUNT(*) INTO v_user_overlap
      FROM concessionaria.usuarios u
      JOIN JSON_TABLE(v_usuarios, '$[*]' COLUMNS(email VARCHAR(255) PATH '$.email')) j
        ON j.email = u.email;

    SELECT COUNT(*) INTO v_user_exact
      FROM concessionaria.usuarios u
      JOIN JSON_TABLE(v_usuarios, '$[*]' COLUMNS(
          nome VARCHAR(255) PATH '$.nome', email VARCHAR(255) PATH '$.email',
          senha VARCHAR(100) PATH '$.senha', role VARCHAR(20) PATH '$.role')) j
        ON j.email = u.email AND j.nome = u.nome AND j.senha = u.senha AND j.role = u.role;

    SELECT COUNT(*) INTO v_cliente_overlap
      FROM concessionaria.clientes c
      JOIN JSON_TABLE(v_clientes, '$[*]' COLUMNS(cpf VARCHAR(20) PATH '$.cpf')) j
        ON j.cpf = c.cpf;

    SELECT COUNT(*) INTO v_cliente_exact
      FROM concessionaria.clientes c
      JOIN JSON_TABLE(v_clientes, '$[*]' COLUMNS(
          nome VARCHAR(255) PATH '$.nome', cpf VARCHAR(20) PATH '$.cpf',
          telefone VARCHAR(50) PATH '$.telefone', email VARCHAR(255) PATH '$.email')) j
        ON j.cpf = c.cpf AND j.nome = c.nome AND j.telefone = c.telefone AND j.email = c.email;

    SELECT COUNT(*) INTO v_veiculo_exact
      FROM concessionaria.veiculos v
      JOIN JSON_TABLE(v_veiculos, '$[*]' COLUMNS(
          marca VARCHAR(100) PATH '$.marca', modelo VARCHAR(100) PATH '$.modelo',
          ano INT PATH '$.ano', cor VARCHAR(100) PATH '$.cor', km BIGINT PATH '$.km' NULL ON EMPTY,
          preco DECIMAL(12,2) PATH '$.preco', status VARCHAR(30) PATH '$.status')) j
        ON j.marca = v.marca AND j.modelo = v.modelo AND j.ano = v.ano AND j.cor = v.cor
       AND j.km <=> v.quilometragem AND j.preco = v.preco AND j.status = v.status;

    SELECT COUNT(*) INTO v_venda_exact
      FROM JSON_TABLE(v_vendas, '$[*]' COLUMNS(
          cliente_ref INT PATH '$.cliente', veiculo_ref VARCHAR(30) PATH '$.veiculo',
          valor DECIMAL(12,2) PATH '$.valor', data_venda DATE PATH '$.data')) sv
      JOIN JSON_TABLE(v_clientes, '$[*]' COLUMNS(ref INT PATH '$.ref', cpf VARCHAR(20) PATH '$.cpf')) sc
        ON sc.ref = sv.cliente_ref
      JOIN JSON_TABLE(v_veiculos, '$[*]' COLUMNS(
          ref VARCHAR(30) PATH '$.ref', marca VARCHAR(100) PATH '$.marca', modelo VARCHAR(100) PATH '$.modelo',
          ano INT PATH '$.ano', cor VARCHAR(100) PATH '$.cor', km BIGINT PATH '$.km' NULL ON EMPTY,
          preco DECIMAL(12,2) PATH '$.preco', status VARCHAR(30) PATH '$.status')) sve
        ON sve.ref = sv.veiculo_ref
      JOIN concessionaria.clientes c ON c.cpf = sc.cpf
      JOIN concessionaria.veiculos v
        ON v.marca = sve.marca AND v.modelo = sve.modelo AND v.ano = sve.ano AND v.cor = sve.cor
       AND v.quilometragem <=> sve.km AND v.preco = sve.preco AND v.status = sve.status
      JOIN concessionaria.tb_vendas venda
        ON venda.cliente_id = c.id AND venda.veiculo_id = v.id
       AND venda.valor = sv.valor AND venda.data_venda = sv.data_venda;

    IF v_user_overlap = 2 AND v_user_exact = 2
       AND v_cliente_overlap = 20 AND v_cliente_exact = 20
       AND v_veiculo_exact = 20 AND v_venda_exact = 15 THEN
        SELECT 'JÁ APLICADO' AS resultado,
               'O conjunto completo já existe. Nenhuma inserção foi realizada.' AS mensagem;
        LEAVE seed;
    END IF;

    IF v_user_overlap > 0 OR v_cliente_overlap > 0
       OR v_veiculo_exact > 0 OR v_venda_exact > 0 THEN
        SELECT 'ABORTADO POR CONFLITO' AS resultado,
               CONCAT('Estado parcial ou ambíguo: usuarios=',v_user_exact,'/',v_user_overlap,
                      ', clientes=',v_cliente_exact,'/',v_cliente_overlap,
                      ', veiculos=',v_veiculo_exact,', vendas=',v_venda_exact,
                      '. Nenhuma inserção foi realizada.') AS mensagem;
        LEAVE seed;
    END IF;

    START TRANSACTION;

    INSERT INTO concessionaria.usuarios (nome, email, senha, role)
    SELECT nome, email, senha, role
      FROM JSON_TABLE(v_usuarios, '$[*]' COLUMNS(
          nome VARCHAR(255) PATH '$.nome', email VARCHAR(255) PATH '$.email',
          senha VARCHAR(100) PATH '$.senha', role VARCHAR(20) PATH '$.role')) j;

    INSERT INTO concessionaria.clientes (nome, cpf, telefone, email)
    SELECT nome, cpf, telefone, email
      FROM JSON_TABLE(v_clientes, '$[*]' COLUMNS(
          nome VARCHAR(255) PATH '$.nome', cpf VARCHAR(20) PATH '$.cpf',
          telefone VARCHAR(50) PATH '$.telefone', email VARCHAR(255) PATH '$.email')) j;

    INSERT INTO concessionaria.veiculos (marca, modelo, ano, cor, quilometragem, preco, status)
    SELECT marca, modelo, ano, cor, km, preco, status
      FROM JSON_TABLE(v_veiculos, '$[*]' COLUMNS(
          marca VARCHAR(100) PATH '$.marca', modelo VARCHAR(100) PATH '$.modelo',
          ano INT PATH '$.ano', cor VARCHAR(100) PATH '$.cor', km BIGINT PATH '$.km' NULL ON EMPTY,
          preco DECIMAL(12,2) PATH '$.preco', status VARCHAR(30) PATH '$.status')) j;

    INSERT INTO concessionaria.tb_vendas (data_venda, valor, cliente_id, veiculo_id)
    SELECT sv.data_venda, sv.valor, c.id, v.id
      FROM JSON_TABLE(v_vendas, '$[*]' COLUMNS(
          cliente_ref INT PATH '$.cliente', veiculo_ref VARCHAR(30) PATH '$.veiculo',
          valor DECIMAL(12,2) PATH '$.valor', data_venda DATE PATH '$.data')) sv
      JOIN JSON_TABLE(v_clientes, '$[*]' COLUMNS(ref INT PATH '$.ref', cpf VARCHAR(20) PATH '$.cpf')) sc
        ON sc.ref = sv.cliente_ref
      JOIN JSON_TABLE(v_veiculos, '$[*]' COLUMNS(
          ref VARCHAR(30) PATH '$.ref', marca VARCHAR(100) PATH '$.marca', modelo VARCHAR(100) PATH '$.modelo',
          ano INT PATH '$.ano', cor VARCHAR(100) PATH '$.cor', km BIGINT PATH '$.km' NULL ON EMPTY,
          preco DECIMAL(12,2) PATH '$.preco', status VARCHAR(30) PATH '$.status')) sve
        ON sve.ref = sv.veiculo_ref AND sve.status = 'VENDIDO'
      JOIN concessionaria.clientes c ON c.cpf = sc.cpf
      JOIN concessionaria.veiculos v
        ON v.marca = sve.marca AND v.modelo = sve.modelo AND v.ano = sve.ano AND v.cor = sve.cor
       AND v.quilometragem <=> sve.km AND v.preco = sve.preco AND v.status = sve.status;

    IF ROW_COUNT() <> 15 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A reconstrução das FKs não produziu exatamente 15 vendas.';
    END IF;

    COMMIT;
    SELECT 'APLICADO' AS resultado,
           'Foram inseridos 2 usuários, 20 clientes, 20 veículos e 15 vendas.' AS mensagem;
END$$

DELIMITER ;
CALL concessionaria.sp_development_seed_20260826();
DROP PROCEDURE IF EXISTS concessionaria.sp_development_seed_20260826;
