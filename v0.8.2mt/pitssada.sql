-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 08/11/2024 às 01:43
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `pitssada`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `bebida`
--

CREATE TABLE `bebida` (
  `id_bebida` int(11) NOT NULL,
  `bebida` varchar(255) DEFAULT NULL,
  `precoBebida` double NOT NULL,
  `qntBebida` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `bebida`
--

INSERT INTO `bebida` (`id_bebida`, `bebida`, `precoBebida`, `qntBebida`) VALUES
(5, 'Sprite 2L', 8.5, 0),
(6, 'Guaraná 600mL', 4.65, 10);

-- --------------------------------------------------------

--
-- Estrutura para tabela `caixa`
--

CREATE TABLE `caixa` (
  `id_caixa` int(11) NOT NULL,
  `id_funcionario` int(11) DEFAULT NULL,
  `caixa` int(11) NOT NULL,
  `nome_funcionario` varchar(255) DEFAULT NULL,
  `abertura` timestamp NOT NULL DEFAULT current_timestamp(),
  `fechamento` timestamp NULL DEFAULT NULL,
  `aberto` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `caixa`
--

INSERT INTO `caixa` (`id_caixa`, `id_funcionario`, `caixa`, `nome_funcionario`, `abertura`, `fechamento`, `aberto`) VALUES
(1, 1, 1, 'João', '2024-11-08 00:09:09', NULL, 0),
(2, 1, 2, 'João', '2024-11-08 00:27:23', NULL, 0),
(3, 1, 2, 'João', '2024-11-08 00:27:37', NULL, 0);

-- --------------------------------------------------------

--
-- Estrutura para tabela `funcionarios`
--

CREATE TABLE `funcionarios` (
  `id_funcionario` int(11) NOT NULL,
  `nome_funcionario` varchar(255) DEFAULT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `idade` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `funcionarios`
--

INSERT INTO `funcionarios` (`id_funcionario`, `nome_funcionario`, `cpf`, `idade`) VALUES
(1, 'João', '999999999-99', 19);

-- --------------------------------------------------------

--
-- Estrutura para tabela `pedido`
--

CREATE TABLE `pedido` (
  `id_pedido` int(11) NOT NULL,
  `sabor` varchar(255) DEFAULT NULL,
  `tamanho` varchar(255) DEFAULT NULL,
  `bebida` varchar(255) DEFAULT NULL,
  `nomeCliente` varchar(255) DEFAULT NULL,
  `rua` varchar(255) DEFAULT NULL,
  `bairro` varchar(255) DEFAULT NULL,
  `numero` int(255) DEFAULT NULL,
  `hora` varchar(255) DEFAULT NULL,
  `precoFinal` double DEFAULT NULL,
  `id_caixa` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `pedido`
--

INSERT INTO `pedido` (`id_pedido`, `sabor`, `tamanho`, `bebida`, `nomeCliente`, `rua`, `bairro`, `numero`, `hora`, `precoFinal`, `id_caixa`) VALUES
(19, 'czars', 'consideravelmente grande', 'Guaraná 600mL', 'César Augusto Bergamin', 'Treza', 'Treza', 18, '19:41:31', 338.65, 1),
(20, 'calabresa', 'médto', 'Sprite 2L', 'Monark', 'dele', 'não sei', 14, '20:02:02', 338.65, 1),
(21, 'ola', 'pequeno', 'Guaraná 600mL', 'Boa noite', 'minha', 'da pitssada', 0, '20:58:59', 338.65, 1),
(22, 'calabresa', 'médio', 'Sprite 2L', 'Hercio', 'sim', 'deele', 61346, '21:37:34', 169.65, 1),
(23, 'czar', 'médio', 'Guaraná 600mL', 'Monark', 'dele', 'sim', 14, '21:33:20', 169.65, 1),
(24, 'czar', 'consideravelmente grande', 'Sprite 2L', 'Czar', 'czar', 'czar', 13, '21:34:59', 169.65, 1),
(25, 'czar', 'médio', 'Sprite 2L', 'Bom dia', 'ola', 'gostaria', 12351346, '19:27:42', 169.65, 1),
(26, 'calabresa', 'médio', 'Sprite 2L', 's\\g', 'i', 'hnoiboib', 235, '19:30:29', 169.65, 1),
(27, 'calabresa', 'médio', 'Sprite 2L', 'asifsjsdo', 'gbiuvbiuvb', 'viuvvvb', 345364, '19:36:53', 169.65, 1),
(28, 'calabresa', 'médio', 'Sprite 2L', 'aergipoja', 'biuobpoiub', 'oiboip', 3467, '19:38:11', 169.65, 1),
(29, 'rrrr', 'consideravelmente grande', 'Guaraná 600mL', 'reger', 'ergergerg', 'ergergerg', 457842, '19:39:21', 169.65, 2);

-- --------------------------------------------------------

--
-- Estrutura para tabela `sabor`
--

CREATE TABLE `sabor` (
  `id_sabor` int(11) NOT NULL,
  `sabor` varchar(255) DEFAULT NULL,
  `precoSabor` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `sabor`
--

INSERT INTO `sabor` (`id_sabor`, `sabor`, `precoSabor`) VALUES
(2, 'calabresa', 15.75),
(3, 'ola', 14),
(4, 'czar', 2),
(5, 'sim', 2134),
(6, 'rrrr', 2),
(7, 'fgdf', 234),
(8, 'monari', 65);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tamanho`
--

CREATE TABLE `tamanho` (
  `id_tamanho` int(11) NOT NULL,
  `tamanho` varchar(255) DEFAULT NULL,
  `precoTamanho` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Despejando dados para a tabela `tamanho`
--

INSERT INTO `tamanho` (`id_tamanho`, `tamanho`, `precoTamanho`) VALUES
(4, 'médio', 40),
(5, 'pequeno', 80),
(6, 'consideravelmente grande', 100);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `bebida`
--
ALTER TABLE `bebida`
  ADD PRIMARY KEY (`id_bebida`);

--
-- Índices de tabela `caixa`
--
ALTER TABLE `caixa`
  ADD PRIMARY KEY (`id_caixa`),
  ADD KEY `id_funcionario` (`id_funcionario`);

--
-- Índices de tabela `funcionarios`
--
ALTER TABLE `funcionarios`
  ADD PRIMARY KEY (`id_funcionario`);

--
-- Índices de tabela `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `caixa` (`id_caixa`);

--
-- Índices de tabela `sabor`
--
ALTER TABLE `sabor`
  ADD PRIMARY KEY (`id_sabor`);

--
-- Índices de tabela `tamanho`
--
ALTER TABLE `tamanho`
  ADD PRIMARY KEY (`id_tamanho`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `bebida`
--
ALTER TABLE `bebida`
  MODIFY `id_bebida` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `caixa`
--
ALTER TABLE `caixa`
  MODIFY `id_caixa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de tabela `funcionarios`
--
ALTER TABLE `funcionarios`
  MODIFY `id_funcionario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de tabela `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT de tabela `sabor`
--
ALTER TABLE `sabor`
  MODIFY `id_sabor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `tamanho`
--
ALTER TABLE `tamanho`
  MODIFY `id_tamanho` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `caixa`
--
ALTER TABLE `caixa`
  ADD CONSTRAINT `caixa_ibfk_1` FOREIGN KEY (`id_funcionario`) REFERENCES `funcionarios` (`id_funcionario`);

--
-- Restrições para tabelas `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`id_caixa`) REFERENCES `caixa` (`id_caixa`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
