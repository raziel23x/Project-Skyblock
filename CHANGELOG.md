Version 1.0.3  

**Raziel23x**

    code clean up and formatiing as well as started added more resource generators

**MeinKraft**

    Tile Gens Update

    Added protection against mods inserting unwanted items in generator blocks
    Clean up of Capability code
    ~ CF

**Raziel23x**

    added RecipeProvider support 

**MeinKraft**

    Mc v1.16.x dev (#7)

     Tile Gens Update

    Added protection against mods inserting unwanted items in generator blocks
    Clean up of Capability code
    ~ CF

    I believe we dont have to specify each block that we want dropped.  We can simply call the 'Super' and let vanilla/other mods work it out
    Seems to work in testing
    Does not work for Beehive/nest
    Adding the shears into the Forge:Shears tag appears to do nothing, via JSON anyways
    unless you have to do it the code like AIOTBotania

Raziel23x

    Finished moving all recipes over to datagen and also moved Loot Tables to Datagen
    Fixed a few derps in recipeprovider as well as a mistake with one of the cobblegens
    New Resource Generators, Fixed a few typos and more changes to improve performace
    clay_generator_block
    endstone_generator_block
    gravel_generator_block
    netherrack_generator_block
    obsidian_generator_block
    quartz_generator_block
    redsand_generator_block
    sand_generator_block
    soulsand_generator_block
    fixed a issue with the quartz model
    Dategen code clean up as well as added Achivements, Recipes, and Loot Tables for the resource generators
    Update Crowdin configuration file
    New Crowdin updates (#8) - all Still need to be translated files have only been generated
    * New translations en_us.json (Romanian)
    * New translations en_us.json (Uyghur)
    * New translations en_us.json (Chinese Simplified)
    * New translations en_us.json (Chinese Traditional)
    * New translations en_us.json (Zulu)
    * New translations en_us.json (English)
    * New translations en_us.json (Urdu (Pakistan))
    * New translations en_us.json (Vietnamese)
    * New translations en_us.json (Portuguese, Brazilian)
    * New translations en_us.json (Thai)
    * New translations en_us.json (Yiddish)
    * New translations en_us.json (Welsh)
    * New translations en_us.json (Tibetan)
    * New translations en_us.json (Venda)
    * New translations en_us.json (Upper Sorbian)
    * New translations en_us.json (Uzbek)
    * New translations en_us.json (Wolof)
    * New translations en_us.json (Walloon)
    * New translations en_us.json (Valencian)
    * New translations en_us.json (Yoruba)
    * New translations en_us.json (Turkish, Cyprus)
    * New translations en_us.json (Tigrinya)
    * New translations en_us.json (Turkmen)
    * New translations en_us.json (Venetian)
    * New translations en_us.json (Zeelandic)
    * New translations en_us.json (Xhosa)
    * New translations en_us.json (Ukrainian)
    * New translations en_us.json (French)
    * New translations en_us.json (Hungarian)
    * New translations en_us.json (Spanish)
    * New translations en_us.json (Afrikaans)
    * New translations en_us.json (Arabic)
    * New translations en_us.json (Catalan)
    * New translations en_us.json (Czech)
    * New translations en_us.json (Danish)
    * New translations en_us.json (German)
    * New translations en_us.json (Greek)
    * New translations en_us.json (Finnish)
    * New translations en_us.json (Hebrew)
    * New translations en_us.json (Italian)
    * New translations en_us.json (Tsonga)
    * New translations en_us.json (Japanese)
    * New translations en_us.json (Korean)
    * New translations en_us.json (Dutch)
    * New translations en_us.json (Norwegian)
    * New translations en_us.json (Polish)
    * New translations en_us.json (Portuguese)
    * New translations en_us.json (Russian)
    * New translations en_us.json (Serbian (Cyrillic))
    * New translations en_us.json (Swedish)
    * New translations en_us.json (Tswana)
    * New translations en_us.json (Turkish)
    * New translations en_us.json (Urdu (India))
    
    Update crowdin.yml
    
    Update CONTRIBUTING.md

    version bump to next release

Version 1.0.2   
    Updated to Forge 1.16.4
    
    Added datagen support for models and other things to make things more easier

Version 1.0.1

**ADDED** "_Repair Gem_",

- "Slowly repairs damaged item in player inventory",

- "Work while gem is in player inventory",

**ADDED** "_Curois support for the Repair Gem in the curio slot_",

**ADDED** "_Block of Red Reagent_",

**ADDED** "_Block of Green Reagent_",

**ADDED** "Block of Blue Reagent",

**ADDED** "_Red Reagent_",

**ADDED** "_Green Reagent_",

**ADDED** "_Blue Reagent_",

**ADDED** "_Mixing Bowl_",

- "Reusable crafting material",

- "Used in creating different types of reagents and materials",

**ADDED** "Cobblestone Generator",

- "Passively generates cobblestone",

- "Can have either a chest placed on top\n as well as be placed on top of a hopper\n or use some type of item transportation",

**ADDED** "_Lava Generator_",

- "Passively generates lava",

- "Use a bucket or use some type of fluid transportation",

**ADDED** "_Water Generator_",

- "Passively generates water",

- "Can be collected with either Buckets, Bottles, Tanks\n or other Fluid containers",

**ADDED** "_Cobblestone Crusher_",

- "An Crushing device - does not work at this time",

- "Can be used in the future to crush and make new reagents",

**ADDED** "_Flint Shovel_",     
**ADDED** "_Flint Sword_",  
**ADDED** "_Flint Pickaxe_",    
**ADDED** "_Flint Hoe_",    
**ADDED** "_Flint Axe_",    
**ADDED** "_Flint Shears_",     
**ADDED** "_Wooden Shears_",        
**ADDED** "_Flint Helmet_",     
**ADDED** "_Flint Boots_",  
**ADDED** "_Flint Leggings_",   
**ADDED** "_Flint Chestplate_",     
**ADDED** "_Wooden Helmet_",    
**ADDED** "_Wooden Boots_",     
**ADDED** "_Wooden Leggings_",  
**ADDED** "_Wooden Chestplate_" 

Version 1.0.0

Initial Release 