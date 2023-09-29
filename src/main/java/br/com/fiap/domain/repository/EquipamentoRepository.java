package br.com.fiap.domain.repository;

import br.com.fiap.domain.entity.Equipamento;
import br.com.fiap.infra.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class EquipamentoRepository implements Repository<Equipamento, Long> {

    private static final AtomicReference<EquipamentoRepository> instance = new AtomicReference<>();

    private EquipamentoRepository() {
    }

    public static EquipamentoRepository build() {
        EquipamentoRepository result = instance.get();
        if (Objects.isNull( result )) {
            EquipamentoRepository repo = new EquipamentoRepository();
            if (instance.compareAndSet( null, repo )) {
                result = repo;
            } else {
                result = instance.get();
            }
        }
        return result;
    }
    @Override
    public Equipamento persist(Equipamento equipamento) {
        var sql = "BEGIN" +
                " INSERT INTO equipamento (NM_EQUIPAMENTO) " +
                "VALUES (?) " +
                "returning ID_EQUIPAMENTO into ?; " +
                "END;" +
                "";



        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();


        CallableStatement cs = null;
        try {
            cs = connection.prepareCall( sql );
            cs.setString( 1, equipamento.getNome() );
            cs.registerOutParameter( 2, Types.BIGINT );
            cs.executeUpdate();
            equipamento.setId( cs.getLong( 2 ) );
            cs.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possível executar o comando!\n" + e.getMessage() );
        }
        return equipamento;
    }

    /**
     * Método que retorna todas as Entidades
     *
     * @return
     */
    @Override
    public List<Equipamento> findAll() {

        List<Equipamento> equipamentos = new ArrayList<>();

        try {

            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM equipamento" );

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong( "ID_EQUIPAMENTO" );
                    String nome = resultSet.getString( "NM_EQUIPAMENTO" );
                    String descrição = resultSet.getString( "DS_EQUIPAMENTO" );
                    //Adicionando equipamentos na coleção
                    equipamentos.add( new Equipamento( id, nome, descrição ) );
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possivel consultar os dados!\n" + e.getMessage() );
        }
        return equipamentos;
    }

    /**
     * Método que retorna uma Entity pelo seu identificador
     *
     * @param id
     * @return
     */
    @Override
    public Equipamento findById(Long id) {
        Equipamento equipamento = null;
        var sql = "SELECT * FROM equipamento where ID_EQUIPAMENTO=?";

        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.setLong( 1, id );
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    equipamento = new Equipamento(
                            resultSet.getLong( "ID_EQUIPAMENTO" ),
                            resultSet.getString( "NM_EQUIPAMENTO" ),
                            resultSet.getString( "DS_EQUIPAMENTO" )
                    );
                }
            } else {
                System.out.println( "Equipamento não encontrado com o id = " + id );
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possível executar a consulta: \n" + e.getMessage() );
        }
        return equipamento;
    }

    @Override
    public Equipamento update(Equipamento equipamento) {
        return null;
    }

    @Override
    public boolean delete(Long id) {.
        return false;
    }

    private List<Equipamento> findByName(String texto) {

        List<Equipamento> equipamentos = new ArrayList<>();
        var sql = "SELECT * FROM equipamento where UPPER(NM_EQUIPAMENTO) like ?";

        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            texto = Objects.nonNull( texto ) ? texto.toUpperCase() : "";
            preparedStatement.setString( 1, "%" + texto + "%" );
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    equipamentos.add(
                            new Equipamento( resultSet.getLong( "ID_EQUIPAMENTO" ), resultSet.getString( "NM_EQUIPAMENTO" ),resultSet.getString( "DS_EQUIPAMENTO" ) )
                    );
                }
            } else {
                System.out.println( "Equipamento não encontrado com o nome = " + texto );
            }
            resultSet.close(); preparedStatement.close(); connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possível executar a consulta: \n" + e.getMessage() );
        }
        return equipamentos;
    }

}
