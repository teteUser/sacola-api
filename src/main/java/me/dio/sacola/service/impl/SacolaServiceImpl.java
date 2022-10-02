package me.dio.sacola.service.impl;

import lombok.RequiredArgsConstructor;
import me.dio.sacola.enums.FormaPagamento;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Restaurante;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.repository.ItemRepository;
import me.dio.sacola.repository.ProdutoRepository;
import me.dio.sacola.repository.SacolaRepository;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {

    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item incluirItemNaSacola(ItemDto itemDto) {
        Sacola sacola = verSacola(itemDto.getIdSacola());

        if(sacola.getFechada()) {
            throw new RuntimeException("Esta sacola está fechada!");
        }

        Item itemParaAdd = Item.builder()
            .quantidade(itemDto.getQuantidade())
            .sacola(sacola)
            .produto(produtoRepository.findById(itemDto.getIdProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado!")))
            .build();

        List<Item> itens = sacola.getItens();
        if(itens.isEmpty()) {
            itens.add(itemParaAdd);
        } else {
            Restaurante restauranteAtual = itens.get(0).getProduto().getRestaurante();
            Restaurante restauranteDoItemParaAdd = itemParaAdd.getProduto().getRestaurante();
            if(restauranteAtual.equals(restauranteDoItemParaAdd)){
                itens.add(itemParaAdd);
            }else{
                throw new RuntimeException("Não é possível adicionar itens de outro restaurante!");
            }
        }

        List<Double> valorDosItens = new ArrayList<>();

        for(Item item : itens){
            Double valorTotalItem = item.getProduto().getValorUnitario() * item.getQuantidade();
            valorDosItens.add(valorTotalItem);
        }

        Double valorTotalSacola = 0D;
        for(Double valorTotalPorItem : valorDosItens){
            valorTotalSacola += valorTotalPorItem;
        }

        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola);
        return itemRepository.save(itemParaAdd);
    }

    @Override
    public Sacola verSacola(Long id) {
        return sacolaRepository.findById(id).orElseThrow(() -> new RuntimeException("Sacola não encontrada!"));
    }

    @Override
    public Sacola fecharSacola(Long id, Integer numeroFormaPagamento) {
        Sacola sacola = verSacola(id);
        if(sacola.getItens().isEmpty()) {
            throw new RuntimeException("Sacola vazia!");
        }
        FormaPagamento formaPagamento = numeroFormaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;
        sacola.setFormaPagamento(formaPagamento);
        sacola.setFechada(true);

        return sacolaRepository.save(sacola);
    }

}
