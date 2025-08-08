package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.DTO.AddArtistDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.ArtistServiceImpl;

@RequestMapping("/artist")
@RestController
public class ArtistController {

    @Autowired
    ArtistServiceImpl artistService;
    @GetMapping
    public ResponseMessage getArtiById(@RequestParam("id") Integer id){
        return artistService.getArtistById(id);
    }

    @PostMapping("/addArtist")
    public ResponseMessage addArtist(@RequestBody AddArtistDTO addArtistDTO){
        return artistService.addArtist(addArtistDTO);
    }

}
