import { Role } from "src/app/models/role";

export interface UserResponse {
    id: number;
    fullName: string;
    address:string;
    phoneNumber:string
    role: Role;    
}