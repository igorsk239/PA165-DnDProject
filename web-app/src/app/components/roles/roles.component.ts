import { Component, OnInit } from '@angular/core';
import {RoleService} from "../../services/role/role.service";
import {Observable} from "rxjs";
import {RoleDTO} from "../../dto/role/RoleDTO";
import {RoleCreateDTO} from "../../dto/role/RoleCreateDTO";


@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.css']
})
export class RolesComponent implements OnInit {
  roles: Observable<RoleDTO>;
  tmp;

  roleCreateDTO: RoleCreateDTO;

  showModal = false;
  mode;

  constructor(private roleService: RoleService) { }

  ngOnInit(): void {
    this.loadRoles();
    this.roleCreateDTO = new RoleCreateDTO();
  }

  loadRoles(){
    this.roleService.getAllRoles().subscribe(response =>{
      this.tmp = response;
      this.roles = this.tmp.content;
    });
  }

  deleteRole(id){
    this.roleService.deleteRole(id).subscribe(response =>{
      this.loadRoles();
    });
  }

  modalPopUp(name){
    this.showModal = !this.showModal;
    this.mode = name;
  }

  createRole(){
    this.roleService.createRole(this.roleCreateDTO)
      .subscribe(data => {this.loadRoles();
      });
    this.roleCreateDTO = new RoleCreateDTO();
    this.showModal = false;
  }


}
